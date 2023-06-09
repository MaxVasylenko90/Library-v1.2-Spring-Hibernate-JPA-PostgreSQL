package ua.vasylenko.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.vasylenko.dao.PersonDao;
import ua.vasylenko.models.Person;
import ua.vasylenko.services.BooksService;
import ua.vasylenko.services.PeopleService;
import ua.vasylenko.util.PersonValidator;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final BooksService booksService;
    private final PersonDao personDao;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, BooksService booksService, PersonDao personDao, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.booksService = booksService;
        this.personDao = personDao;
        this.personValidator = personValidator;
    }

    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("peopleList", peopleService.getPeople());
        return "people/index";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("person") Person person) {
        return "people/newUser";
    }

    @PostMapping()
    public String createNewUser(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors())
            return "people/newUser";
        peopleService.create(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}")
    public String userProfile(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.getPerson(id));
        model.addAttribute("books", peopleService.getBooksByPerson(id));
        return "people/userPage";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") int userId, Model model) {
        model.addAttribute("user", peopleService.getPerson(userId));
        return "people/editUser";
    }

    @PatchMapping("/{id}")
    public String updateUser(@ModelAttribute("user")Person person, @PathVariable("id") int userId) {
        peopleService.update(userId, person);
        return "redirect:/people/{id}";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") int userId) {
        peopleService.delete(userId);
        return "redirect:/people";
    }
}