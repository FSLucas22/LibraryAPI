package com.flucas.libraryapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.service.interfaces.EmailService;
import com.flucas.libraryapi.service.interfaces.LoanService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SchedulerService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateloans.message}")
    private String message;
    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans() {
        List<Loan> lateLoans = loanService.getAllLateLoans();
        var emails = lateLoans.stream().map(Loan::getEmail).collect(Collectors.toList());
        emailService.sendMails(emails, message);
    }
}
