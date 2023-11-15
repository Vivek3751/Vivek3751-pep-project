package Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);
        //post rqst for new User's account Registration
        app.post("/register",this::newUserRegisterHandler);
        //post rqst for User Login 
        app.post("/login",this::verifyLoginHandler);
        //post rqst for Create Message
        app.post("/messages",this::createMessageHandler);
        //get rqst for retreiving all messages
        app.get("/messages",this::getAllMessagesHandler);
        //submiting a get rqst to retrieve a message by its ID.
        app.get("/messages/{message_id}",this::getMessageByIdHandler);
        //submiting a delete Rqst to delete a message identified by a message ID
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        //submiting a patch rqst to update a msg text by messadeID
        app.patch("/messages/{message_id}", this::updateMessageTextHandler);
        //submiting get rqst to retrieve all msgs written by user
        app.get("/accounts/{account_id}/messages",this::getMessagesByUserHandler);
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }
    /*Register new user handle
    As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register. The body will contain a representation of a JSON Account, but will not contain an account_id.

- The registration will be successful if and only if the username is not blank, the password is at least 4 characters long, and an Account with that username does not already exist. If all these conditions are met, the response body should contain a JSON of the Account, including its account_id. The response status should be 200 OK, which is the default. The new account should be persisted to the database.
- If the registration is not successful, the response status should be 400. (Client error)*/

    private void newUserRegisterHandler(Context context) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);

        if((!account.getUsername().isBlank()) && (account.getPassword().length()>=4) && (!(account.getUsername()).equals(accountService.getUserNameString(account)))){
            Account addedUser = accountService.newUserRegister(account);
            if (addedUser != null) {
                context.json(mapper.writeValueAsString(addedUser)); 
            }                      
        }
        else{
            context.status(400);
        }               
    }
    /* 2: Our API should be able to process User logins.
    As a user, I should be able to verify my login on the endpoint POST localhost:8080/login. The request body will contain a JSON representation of an Account, not containing an account_id. In the future, this action may generate a Session token to allow the user to securely use the site. We will not worry about this for now.
- The login will be successful if and only if the username and password provided in the request body JSON match a real account existing on the database. If successful, the response body should contain a JSON of the account in the response body, including its account_id. The response status should be 200 OK, which is the default.
- If the login is not successful, the response status should be 401. (Unauthorized) */
    private void verifyLoginHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(context.body(), Account.class);
        String storedUsername = accountService.getUserNameString(account);

        if (storedUsername != null && storedUsername.equals(account.getUsername())) {
        // User exists, now check if the password matches
        Account storedAccount = accountService.getAccountByUsername(account.getUsername());

        if (storedAccount != null && storedAccount.getPassword().equals(account.getPassword())) {
            // Login successful
            context.json(mapper.writeValueAsString(storedAccount));
        } else {
            // Password doesn't match
            context.status(401);
        }
        } 
        else {
        // User doesn't exist
        context.status(401);
        }
    }

    /*3: Our API should be able to process the creation of new messages.

As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. The request body will contain a JSON representation of a message, which should be persisted to the database, but will not contain a message_id.

- The creation of the message will be successful if and only if the message_text is not blank, is under 255 characters, and posted_by refers to a real, existing user. If successful, the response body should contain a JSON of the message, including its message_id. The response status should be 200, which is the default. The new message should be persisted to the database.
- If the creation of the message is not successful, the response status should be 400. (Client error) */
    private void createMessageHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
    
        // Validate message_text length and posted_by existence
        if (!message.getMessage_text().isBlank() && message.getMessage_text().length() <= 255) {
        // Check if the posted_by user exists
        Account postedByUser = accountService.getAccountById(message.getPosted_by());

        if (postedByUser != null) {
            // Create and persist the message
            Message createdMessage = messageService.createMessage(message);
            if (createdMessage != null) {
                context.json(mapper.writeValueAsString(createdMessage));
            } else {
                // Failed to create message
                context.status(500); // Internal Server Error
            }
        } else {
            // Invalid posted_by user
            context.status(400); // Bad Request
        }
        } else {
        // Invalid message_text length
        context.status(400); // Bad Request
        }
    }
    /*## 4: Our API should be able to retrieve all messages.
    As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.
    - The response body should contain a JSON representation of a list containing 
    all messages retrieved from the database. It is expected for the list to simply be empty if there are no messages.
    The response status should always be 200, which is the default. */
    private void getAllMessagesHandler(Context context) throws JsonProcessingException {
        List<Message> allMessages = messageService.getAllMessages();

        ObjectMapper mapper = new ObjectMapper();
        context.json(mapper.writeValueAsString(allMessages)).status(200);
    }
    /*5: Our API should be able to retrieve a message by its ID.
    As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.
    - The response body should contain a JSON representation of the message identified by the message_id.
    It is expected for the response body to simply be empty if there is no such message.
    The response status should always be 200, which is the default. */
    private void getMessageByIdHandler(Context context) throws JsonProcessingException {
        int messageId = context.pathParamAsClass("message_id", Integer.class).get();
        Message message = messageService.getMessageById(messageId);

        ObjectMapper mapper = new ObjectMapper();
        if (message != null) {
            context.json(mapper.writeValueAsString(message)).status(200);
        } else {
            // If no such message, return an empty response with status 200
            context.result("").status(200);
        }
    }
    /*6: Our API should be able to delete a message identified by a message ID.
    As a User, I should be able to submit a DELETE request on the endpoint DELETE localhost:8080/messages/{message_id}.
    - The deletion of an existing message should remove an existing message from the database. If the message existed, the response body should contain the now-deleted message. The response status should be 200, which is the default.
    - If the message did not exist, the response status should be 200, but the response body should be empty. This is because the DELETE verb is intended to be idempotent, ie,
    multiple calls to the DELETE endpoint should respond with the same type of response. */
    private void deleteMessageByIdHandler(Context context) throws JsonProcessingException {
        int messageId = context.pathParamAsClass("message_id", Integer.class).get();
        //int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessageById(messageId);
        ObjectMapper mapper = new ObjectMapper();
        if (deletedMessage != null) {
            context.json(mapper.writeValueAsString(deletedMessage)).status(200);
        } else {
            // If the message did not exist, return an empty response with status 200
            context.result("").status(200);
        }
    }
    /*7: Our API should be able to update a message text identified by a message ID.As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{message_id}. The request body should contain a new message_text values 
    to replace the message identified by message_id.
    The request body can not be guaranteed to contain any other information.
    - The update of a message should be successful if and only if the message id already exists and the new message_text is not blank and is not over 255 characters. If the update is successful, the response body should contain the full updated message (including message_id, posted_by, message_text, and time_posted_epoch), and the response status should be 200, which is the default. The message existing on the database should have the updated message_text.
    - If the update of the message is not successful for any reason, the response status should be 400. (Client error) */
    private void updateMessageTextHandler(Context context) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(context.body(), Message.class);
        int messageId = context.pathParamAsClass("message_id", Integer.class).get();
        Message updatedMessage = messageService.updateMessageText(messageId,message.getMessage_text());

        
        if (updatedMessage != null) {
            context.json(mapper.writeValueAsString(updatedMessage)).status(200);
        } else {
            // If the update is not successful, return a 400 Bad Request response
            context.status(400);
        }
    }
    /*8: Our API should be able to retrieve all messages written by a particular user.
    As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{account_id}/messages.
    - The response body should contain a JSON representation of a list containing all messages posted by a particular user,
    which is retrieved from the database. It is expected for the list to simply be empty if there are no messages.
    The response status should always be 200, which is the default. */
    private void getMessagesByUserHandler(Context context) throws JsonProcessingException {
        Integer accountId = context.pathParamAsClass("account_id", Integer.class).get();
        List<Message> messagesByUser = messageService.getMessagesByUser(accountId);

        ObjectMapper mapper = new ObjectMapper();
        context.json(mapper.writeValueAsString(messagesByUser)).status(200);
    }

}