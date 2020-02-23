package com.ecommerce.ecommApp.notifications.handlers;

import com.ecommerce.ecommApp.EcommAppApplication;
import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.enums.NotificationType;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderCancelled;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderPlaced;
import com.ecommerce.ecommApp.commons.pojo.notification.UserRegistered;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Sagar Bindal
 * Notification Handler Class helps in sending Notification's.
 */
public class NotificationHandler implements Handler {

    private static final Logger log=LoggerFactory.getLogger(NotificationHandler.class);

    /**
     * This Method will be the entry Point for any notification service to send notification.
     * When some record comes this services checks via which mode we need to notify user.
     * @param notifyingService : This refers to the Notifying Service name. It can be User_Registered, Order_Placed, Order_Cancelled, Order_status
     * @param modes : This refers by what type notification service needs to notify the Customer. Its types are mentioned in NotificationType Enum.
     * @param object : This is the object which which be send via notification service which will contain the dtails required to send notification to the user.
     * @param message : Message to be send.
     */
    @Override
    public void sendNotification(String notifyingService,List<String> modes, Object object, String message) {
        if (modes.contains(NotificationType.Text_SMS.toString()))
            createSmsNotification(notifyingService, object, message);

        if (modes.contains(NotificationType.EMAIL.toString()))
            createEmailNotificaton(notifyingService, object, message);

        if (modes.contains(NotificationType.Text_WHATSAPP.toString()))
            createWhatsappNotification(notifyingService, object, message);
    }

    /**
     * This method works as a channel to create the Sms Notification.
     */
    public void createSmsNotification(String notifyingService,Object object,String message) {
        switch(notifyingService)
        {
            case CommonsUtil.NOTIFICATION_USER_REGISTERED_SERVICE:
                sendSmsNotification(((UserRegistered)(object)).getCustomerDto().getNumber(),message);
                break;
            case CommonsUtil.NOTIFICATION_ORDER_CANCELLED_SERVICE:
                sendSmsNotification(((OrderCancelled)(object)).getCustomerDto().getNumber(),message);
                break;
            case CommonsUtil.NOTIFICATION_ORDER_STATUS_SERVICE:
                sendSmsNotification(((UserRegistered)(object)).getCustomerDto().getNumber(),message);
                break;
            case CommonsUtil.NOTIFICATION_ORDER_PLACED_SERVICE:
                sendSmsNotification(((OrderPlaced)(object)).getCustomerDto().getNumber(),message);
                break;
        }
    }

    /**
     * This method works as a channel to create the Email Notification.
     */
    public void createEmailNotificaton(String notifyingService, Object object,String message) {
        switch (notifyingService) {
            case CommonsUtil.NOTIFICATION_USER_REGISTERED_SERVICE:
                sendEmailNotificaton(((UserRegistered) (object)).getCustomerDto().getEmail(), notifyingService, message);
                break;
            case CommonsUtil.NOTIFICATION_ORDER_CANCELLED_SERVICE:
                sendEmailNotificaton(((OrderCancelled) (object)).getCustomerDto().getEmail(), notifyingService, message);
                break;
            case CommonsUtil.NOTIFICATION_ORDER_STATUS_SERVICE:
                sendEmailNotificaton(((UserRegistered) (object)).getCustomerDto().getEmail(), notifyingService, message);
                break;
            case CommonsUtil.NOTIFICATION_ORDER_PLACED_SERVICE:
                sendEmailNotificaton(((OrderPlaced) (object)).getCustomerDto().getEmail(), notifyingService, message);
                break;
        }
    }

    /**
     * This method works as a channel to create the Whatsapp Notification.
     */
    public void createWhatsappNotification(String notifyingService, Object object,String message) {
        // TODO : integrate the whatsapp.
        log.trace("Sending Whatapp Notification");
    }

    /**
     * This Method actually sends notification to the provided number using the twilio SDK.
     * @param number : Cell Phone Number to which we need to send SMS
     * @param message : Message to be send
     */
    public void sendSmsNotification(Long number, String message) {
        if (number == null)
            return;

        try {
            log.trace("Sending Sms Notification with {} message to {}", message, number);
            Message sms = Message.creator(new PhoneNumber("+91" + number),
                    new PhoneNumber(EcommAppApplication.environment.getRequiredProperty(CommonsUtil.TWILIO_ASSIGNED_NUMBER)),
                    message).create();
            log.info("Sms Notification Sent to {} ", number);
        } catch (ApiException ex) {
            log.error("Twilio API Exception : Error in sending message to {}", number);
        }
    }

    /**
     * This method send email notification to the particular email using the SendGrid SDK.
     * @param email : Email to which we need to send the Notification
     * @param subject : Subject of teh Email is basically the Name of the Notification Service
     * @param message : Message to be send.
     */
    public void sendEmailNotificaton(String email, String subject, String message) {

        if (email.trim().equals(""))
            return;
        log.trace("Sending Email Notification with {} message to {}", message, email);
        Email from = new Email(EcommAppApplication.environment.getRequiredProperty(CommonsUtil.SENDGRID_FROM_EMAIL));
        Email to = new Email(email);
        Content content = new Content("text/plain", message);

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(CommonsUtil.SENDGRID_ACCESS_TOKEN);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info("Email Notification Sent to {} ", email);
        } catch (Exception ex) {
            log.error("SendGrid Exception : Error in sending email to {} with mesasge {}", email,ex.getMessage());
        }
    }

    /**
     * @param number : Number to send WhatsApp Notification
     * @param message : Message to be send.
     */
    public void sendWhatsappNotification(Long number, String message) {
        if (number == null)
            return;
        // TODO Integrate suitable whatsapp notification SDK
    }
}
