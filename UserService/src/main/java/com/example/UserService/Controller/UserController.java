package com.example.UserService.Controller;


import com.example.UserService.Dto.LoginDto;
import com.example.UserService.Model.User;
import com.example.UserService.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Map;

@RestController
//@RequestMapping(path = "/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String helloWorld(){
        return "Hello World from Spring Boot";
    }


    //get all users
    @GetMapping(
            value = "/users",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(){
        ArrayList<User> users = userService.getAllUsers();
        if(users.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<ArrayList<User>>(userService.getAllUsers(), HttpStatus.OK);
    }

    //create (register) user
    @PostMapping(
            value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody User newUser){
        try {
            return new ResponseEntity<User>(userService.create(newUser) , HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
    //login user
    @PostMapping(path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        try{
            return new ResponseEntity<User>(userService.login(loginDto.getUsername(), loginDto.getPassword()), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //get user by username
    @GetMapping(
            value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByUsername(@RequestParam(value="username") String username){
        try{
            return new ResponseEntity<User>(userService.findByUsername(username), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //get user by email
    @GetMapping(value="/userByEmail",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserByEmail(@RequestParam(value="email") String email){
        try{
            return new ResponseEntity<User>(userService.findByEmail(email), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //delete user by username
    @DeleteMapping(value = "/user",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUserByUsername(@RequestParam(value="username") String username){
        try{
            userService.deleteUserByUsername(username);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("User deleted by username", HttpStatus.OK);
    }
    //delete user by email
    @DeleteMapping(value = "/userByEmail",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUserByEmail(@RequestParam(value="email") String email){
        try{
            userService.deleteUserByEmail(email);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>("User deleted by email", HttpStatus.OK);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") String userId,
                                                           @RequestBody User user)
    {
        return userService.updateUser(userId, user);
    }

    //get all users by part of username
    @GetMapping(path = "/search/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchForUsername(@PathVariable("username") String username){
        ArrayList<User> users = userService.searchUserByUsername(username);
        if(users.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<ArrayList<User>>(users, HttpStatus.OK);
    }
    //follow user
    @PutMapping(path = "/follow",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> follow(@RequestBody Map<String, String> followRequest){
        try{
            return new ResponseEntity<User>(userService.follow(followRequest.get("followerId"), followRequest.get("toFollowId")), HttpStatus.OK);
        } catch (IllegalStateException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    //block user
    @PutMapping(path = "/block",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> block(@RequestBody Map<String, String> block){
        try{
            return new ResponseEntity<User>(userService.block(block.get("blockerId"), block.get("blockedId")), HttpStatus.OK);
        } catch (IllegalStateException e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(path = "/approve",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> approveFollow(@RequestBody Map<String, String> userIds) {
        if (userService.approveFollow(userIds.get("userId"), userIds.get("followerUserId"))) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }
}
