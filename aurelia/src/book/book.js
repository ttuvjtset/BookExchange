import { HttpClient, json } from 'aurelia-fetch-client';
import { customAttribute, bindable, inject } from 'aurelia-framework';
import { Router } from 'aurelia-router';
import { Connector } from 'auth/connector';
import { Authorization } from 'auth/authorization';
import environment from '../environment';
import {activationStrategy} from "aurelia-router";


let httpClient = new HttpClient();

@inject(Router, Connector, Authorization)
export class Book {

  constructor(router, connector, authorization) {
    this.bookbyid = null;
    this.router = router;
    this.connector = connector;
    this.authorization = authorization;
    this.loggedIn = false;
    this.noBookFound = false;
    this.bookFound = false;
    this.fetchingBookFromApi = true;
    this.fetchBooksForWatchList();
    this.addedToWatchlist = [];
    this.bookAlreadyInWathList = false;
  }

  activate(params) {
    this.id = params.id;
  }

  attached() {
    this.fetchBookByIdFromAPI();

    $(window).on('popstate', function (event) {
      if ($.featherlight.current()) { 
        $.featherlight.current().close(); 
      }
    }); 
  }

  determineActivationStrategy() {
    return activationStrategy.replace;
  }

  fetchBooksForWatchList() {
    httpClient.fetch(environment.apiURL + 'api/users/getwatchlist?session=' + this.authorization.getSessionID())
      .then(response => response.json())
      .then(data => {
        Object.entries(data).forEach(
          ([key, value]) => {
            if (!this.addedToWatchlist.includes(value.bookid.id)) this.addedToWatchlist.push(value.bookid.id);
          }
        );
        //console.log(this.addedToWatchlist);
       // console.log(typeof this.bookAlreadyInWathList);
       // console.log(typeof Number.parseInt(this.id));
        if (this.addedToWatchlist.includes(Number.parseInt(this.id))) {
          this.bookAlreadyInWathList = true;
          //console.log("includes!");
        }
      });
  }

  fetchBookByIdFromAPI() {
    this.authorization.isLoggedIn().then(data => {
      let apiURL = environment.apiURL + 'api/books/getinfoid?id=' + this.id;
      if (!data.errors) {
        this.loggedIn = true;
        let userSession = this.authorization.getSessionID();
        apiURL += "&session=" + userSession;
      }
      httpClient.fetch(apiURL)
      .then(response => {
        this.fetchingBookFromApi = false;
        if (response.status !== 500) {
          return response.json();
        } else {
          this.noBookFound = true;
          throw Error(response.statusText); // 500 error -> no book found
        }
      })
      .then(data => {
        this.bookbyid = data;
        this.bookFound = true;
      }).catch(error => {
        console.log("Mitte ükski raamat ei leitud");
      });
    });
  }

  ifJSONAttributeIsNull(text) {
    return text === null;
  }

  convertUnixTimeStamp(unixTimeStamp) {
    let date = new Date(unixTimeStamp);
    let options = { day: 'numeric', month: 'long', year: 'numeric' };
    return "Lisatud " + date.toLocaleTimeString('et-EE', options);
  }

  addToWatchList(bookID) {
    if (!this.connector.loggedIn) {
      this.router.navigateToRoute('login');
    } else {
      let userSession = this.authorization.getSessionID();
      httpClient.fetch(environment.apiURL + 'api/users/addtowatchlist?session=' + userSession + '&bookid=' + bookID)
      .then(response => response.json())
        .then(data => {
          if (data.errors.length === 0) {
            this.fetchBookByIdFromAPI();
            this.fetchBooksForWatchList();
            $.uiAlert({
              textHead: 'Õnnestus!',
              text: 'Raamat oli lisatud Teie Jälgimisnimekirja!',
              bgcolor: '#19c3aa',
              textcolor: '#fff',
              position: 'bottom-left',
              icon: 'checkmark box',
              time: 4,
            })
          } else if (data.errors.includes("FAIL_EXISTS_BOOKID")) {
            $.uiAlert({
              textHead: 'Lisamise viga',
              text: 'See raamat on juba olemas Teie Jälgimisnimekirjas',
              bgcolor: '#55a9ee',
              textcolor: '#fff',
              position: 'bottom-left',
              icon: 'info circle',
              time: 4,
            })
          } else {
            $.uiAlert({
              textHead: 'API viga',
              text: 'Ei saanud lisada raamatut Teie Jälgimisnimekirja',
              bgcolor: '#F2711C',
              textcolor: '#fff',
              position: 'bottom-left',
              icon: 'warning sign',
              time: 4,
            })
          }
        });
    }
  }

}
