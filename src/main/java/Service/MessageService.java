package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    private MessageDAO messageDAO;
    /*No Args */
    public MessageService(){
        messageDAO=new MessageDAO();
    }
    public Message createMessage(Message message){
        return messageDAO.createMessage(message);
    }
    public List<Message> getAllMessages(){
        return messageDAO.getAllMessages();
    }
    public Message getMessageById(int messageId){
        return messageDAO.getMessageById(messageId);
    }
    public Message deleteMessageById(int messageId){
        return messageDAO.deleteMessageById(messageId);
    }
    public Message updateMessageText(int messageId, String newMessageText) {
        return messageDAO.updateMessageText(messageId, newMessageText);
    }
    public List<Message> getMessagesByUser(int accountId) {
        return messageDAO.getMessagesByUser(accountId);
    }
}
