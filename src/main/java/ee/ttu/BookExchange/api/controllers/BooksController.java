package ee.ttu.BookExchange.api.controllers;

import ee.ttu.BookExchange.api.models.Books;
import ee.ttu.BookExchange.api.services.BooksService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/books", produces = "application/json")
public class BooksController {
    private BooksService booksService;

    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    HashMap<String, Books> addBook(@RequestBody Books inputBook) {
        inputBook.setUserid("Guest");
        booksService.saveBook(inputBook);
        return new HashMap<>();
    }

    private String[] parseArrayString(String arrayString) {
        String[] output = {};
        if (arrayString.charAt(0) == '[' && arrayString.charAt(arrayString.length() - 1) == ']') {
            output = arrayString.substring(1, arrayString.length() - 1).split(Pattern.quote(","));
        } else {
            output = new String[1];
            output[0] = arrayString;
        }
        return output;
    }

    private String getStringOrEmptyString(String input) {
        return (input == null) ? "" : input;
    }

    @RequestMapping(value = "something", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String getSomething(@RequestParam Map<String,String> requestParams,
                               @RequestParam(value = "sort") Optional<String> sortMask,
                               @RequestParam(value = "sortdesc") Optional<Boolean> isSortDesc)
    {
        String output = "";
        System.out.println("isPresent: " + isSortDesc.isPresent());
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            System.out.println(entry.getKey());
            String[] arr = parseArrayString(entry.getValue());
            System.out.print("[ ");
            for (String str : arr) {
                System.out.print(str + ", ");
            }
            System.out.println(" ]");
        }

        output += "<br>" + requestParams.toString();
        return output;
    }

    @RequestMapping(value = "getall", method = RequestMethod.GET)
    public Map<String, List<Books>> getAllBooks(
            @RequestParam Map<String,String> requestParams,
            @RequestParam(value = "sort") Optional<String> sortMask,
            @RequestParam(value = "sortdesc") Optional<Boolean> isSortDesc)
    {
        int paramsSize = 0;
        if (sortMask.isPresent())
            paramsSize++;
        if (isSortDesc.isPresent())
            paramsSize++;

        if (!isSortDesc.isPresent())
            isSortDesc = Optional.of(false);
        Map<String, List<Books>> map = new HashMap<>();
        List<Books> allBooks = booksService.getAllBooks();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            String[] valueArray = parseArrayString(entry.getValue());
            if (entry.getKey().equals("sort") || entry.getKey().equals("sortdesc"))
                continue;

            List<Books> filteredBooks;
            if (requestParams.size() == paramsSize)
                filteredBooks = allBooks;
            else
                filteredBooks = new ArrayList<>();
            for (String value : valueArray) {
                filteredBooks.addAll(allBooks.stream()
                        .filter(e -> {
                            switch (entry.getKey()) {
                                case "id":
                                    return Integer.toString(e.getId()).equals(value);
                                case "title":
                                    return e.getTitle().equals(value);
                                case "author":
                                    return e.getAuthor() != null && e.getAuthor().equals(value);
                                case "description":
                                    return e.getDescription().equals(value);
                                case "conditiondesc":
                                    return e.getConditiondesc() != null && e.getConditiondesc().equals(value);
                                case "price":
                                    return e.getPrice().toString().equals(value);
                                case "likes":
                                    return Integer.toString(e.getLikes()).equals(value);
                                case "isbn":
                                    return e.getIsbn() != null && e.getIsbn().equals(value);
                                case "imagepath":
                                    return e.getImagepath().equals(value);
                                case "publisher":
                                    return e.getPublisher() != null && e.getPublisher().equals(value);
                                case "pubyear":
                                    return e.getPubyear() != null && e.getPubyear().equals(value);
                                case "language":
                                    return e.getLanguage() != null && e.getLanguage().equals(value);
                                case "postdate":
                                    return e.getPostdate().toString().equals(value);
                                case "userid":
                                    return e.getUserid().equals(value);
                                case "genreid":
                                    return e.getGenreid() != null && e.getGenreid().equals(value);
                                case "city":
                                    return e.getCity() != null && e.getCity().equals(value);
                                default:
                                    return false;
                            }
                        })
                        .collect(Collectors.toList()));
            }
            allBooks = filteredBooks;
        }
        if (sortMask.isPresent()) {
            allBooks = allBooks.stream()
                    .sorted((e1, e2) -> {
                        switch (sortMask.get()) {
                            case "id":
                                return Integer.compare(e1.getId(), e2.getId());
                            case "title":
                                return e1.getTitle().compareTo(e2.getTitle());
                            case "author":
                                return getStringOrEmptyString(e1.getAuthor())
                                        .compareTo(getStringOrEmptyString(e2.getAuthor()));
                            case "description":
                                return e1.getDescription().compareTo(e2.getDescription());
                            case "conditiondesc":
                                return getStringOrEmptyString(e1.getConditiondesc())
                                        .compareTo(getStringOrEmptyString(e2.getConditiondesc()));
                            case "price":
                                return e1.getPrice().toString().compareTo(e2.getPrice().toString());
                            case "likes":
                                return Integer.compare(e1.getLikes(), e2.getLikes());
                            case "isbn":
                                return getStringOrEmptyString(e1.getIsbn())
                                        .compareTo(getStringOrEmptyString(e2.getIsbn()));
                            case "imagepath":
                                return e1.getImagepath().compareTo(e2.getImagepath());
                            case "publisher":
                                return getStringOrEmptyString(e1.getPublisher())
                                        .compareTo(getStringOrEmptyString(e2.getPublisher()));
                            case "pubyear":
                                return getStringOrEmptyString(e1.getPubyear())
                                        .compareTo(getStringOrEmptyString(e2.getPubyear()));
                            case "language":
                                return getStringOrEmptyString(e1.getLanguage())
                                        .compareTo(getStringOrEmptyString(e2.getLanguage()));
                            case "postdate":
                                return e1.getPostdate().toString().compareTo(e2.getPostdate().toString());
                            case "userid":
                                return e1.getUserid().compareTo(e2.getUserid());
                            case "genreid":
                                return getStringOrEmptyString(e1.getGenreid())
                                        .compareTo(getStringOrEmptyString(e2.getGenreid()));
                            case "city":
                                return getStringOrEmptyString(e1.getCity())
                                        .compareTo(getStringOrEmptyString(e2.getCity()));
                            default:
                                return 0;
                        }
                    })
                    .collect(Collectors.toList());
        }
        if (isSortDesc.get())
            Collections.reverse(allBooks);
        map.put("books", allBooks);
        map.put("errors", new ArrayList<>());
        return map;
        //return booksService.getAllBooks();
    }

    @RequestMapping(value = "getinfoid", method = RequestMethod.GET)
    public Books getBook(@RequestParam(value = "id") int bookId) {
        return booksService.getBookById(bookId);
    }
}
