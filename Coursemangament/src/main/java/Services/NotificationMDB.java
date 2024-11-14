package Services;

import Entities.Enrollment;
import Entities.Notification;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/NotificationQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
        }
)
public class NotificationMDB implements MessageListener {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String[] content = ((TextMessage) message).getText().split(";");
                String notificationMessage = content[0];
                Long enrollmentId = Long.parseLong(content[1]);

                Enrollment enrollment = em.find(Enrollment.class, enrollmentId);
                if (enrollment != null) {
                    Notification notification = new Notification(notificationMessage, enrollment);
                    notification.setTimestamp(new Date());
                    em.persist(notification);
                } else {
                    System.err.println("Enrollment with ID " + enrollmentId + " not found.");
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
