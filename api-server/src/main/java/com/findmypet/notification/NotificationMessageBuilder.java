package com.findmypet.notification;

public class NotificationMessageBuilder {

    public static String buildInquiryCreatedMessage(String senderName) {
        return senderName + "님의 문의가 도착했어요!";
    }

    public static String buildInquiryReplyMessage(String senderName) {
        return senderName + "님의 답변이 도착했어요!";
    }
}
