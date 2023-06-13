package com.flucas.libraryapi.service.interfaces;

import java.util.List;

public interface EmailService {

    void sendMails(List<String> emails, String message);

}
