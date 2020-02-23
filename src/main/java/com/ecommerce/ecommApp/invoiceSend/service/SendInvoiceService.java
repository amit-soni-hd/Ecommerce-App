package com.ecommerce.ecommApp.invoiceSend.service;

import com.ecommerce.ecommApp.invoiceSend.dto.SendInvoiceDto;
import com.ecommerce.ecommApp.invoiceSend.utils.SendUtils;
import com.google.common.io.Files;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGridAPI;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
public class SendInvoiceService {

    @Autowired
    private SendGridAPI sendGridAPI;


    public Response sendInvoice(SendInvoiceDto sendInvoiceDto)  {

        Email from = new Email(SendUtils.VENDOR_EMAIL, SendUtils.VENDOR_NAME);
        Email to = new Email(sendInvoiceDto.getTo(), sendInvoiceDto.getCustomerName());
        String message = sendInvoiceDto.getContentMessage();
        String subject = sendInvoiceDto.getSubject();
        Content content = new Content(SendUtils.CONTENT_TYPE, message);

        Mail mail = new Mail(from, subject, to, content);
        Request request = new Request();

        mail.addAttachments(getAttachments(sendInvoiceDto.getFile()));

        request.setMethod(Method.POST);
        request.setEndpoint(SendUtils.END_POINT);
        Response response = null;

        try {

            request.setBody(mail.build());
            response = sendGridAPI.api(request);

        } catch (IOException e) {
            e.printStackTrace();
            throw  new RuntimeException("Exception with build or send the mail with message : " + e.getMessage());
        }


        log.trace("Send mail with response code : " + response.getStatusCode());

        return response;
    }

    private Attachments getAttachments(File file)  {

        Attachments attachment = new Attachments();

        try {
            attachment.setContent(convertFileToBase64(Files.toByteArray(file)));
            attachment.setType(SendUtils.SEND_TYPE);
            attachment.setFilename(SendUtils.FILE_NAME + SendUtils.FILE_EXTENSION);

        } catch (IOException e) {
            log.info("File not found for attaching the customer invoice ,file path is  : {}", file.getPath());
            e.printStackTrace();
            throw  new RuntimeException("File not fount exception " + e.getMessage() + " Cause : " + e.getCause());
        }

        return attachment;
    }

    private String convertFileToBase64(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }
}
