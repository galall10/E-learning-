package Services;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

@Stateless
public class NotificationProducer {

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:/jms/queue/NotificationQueue")
    private Queue notificationQueue;

    public void sendNotification(String message, Long enrollmentId) {
        jmsContext.createProducer().send(notificationQueue, message + ";" + enrollmentId);
    }
}
