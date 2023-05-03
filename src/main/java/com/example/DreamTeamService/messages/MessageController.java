package com.example.DreamTeamService.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    MessageRepository messageRepository;
    //endopoint pozwalajacy pobrac widomosci wymienianie z danym uzytkownikiem
    @GetMapping("")
    public List<Message> getMessages(@RequestParam String username){
        Object user1 = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username2 = user1.toString();
        return messageRepository.getMessages(username, username2);
    }
    //endpoint umozliwiajacy wyslanie wiadomosci
    @PostMapping("")
    public boolean sendMessage(@RequestBody Message message, HttpServletResponse response){
        Object sender = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(!sender.toString().equals(message.getSender()) || message.getSender().equals(message.getReceiver())){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        boolean isMessageSend = messageRepository.sendMessage(message);
        if(isMessageSend){
            return true;
        }
        else {
            //jesli cos poszlo nie tak ustaw status 406
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return false;
        }
    }
    //enpoint umozliwiajacy pobranie listy znajomych uzytkownikow
    @GetMapping("/friends")
    public List<Friend> getFriends(){
        Object user = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = user.toString();
        return messageRepository.getFriends(username);
    }
    //endpoint umozliwiajacy zmiane statusu wiadomosci na przeczytana
    @PatchMapping("/{senderUsername}")
    public boolean changeStatus(@PathVariable String senderUsername){
        Object user = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String receiverUsername = user.toString();
        return messageRepository.changeStatus(receiverUsername, senderUsername);
    }
}