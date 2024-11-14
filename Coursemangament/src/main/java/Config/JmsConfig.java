package Config;

import jakarta.jms.JMSDestinationDefinition;

@JMSDestinationDefinition(
        name = "java:/jms/queue/NotificationQueue",
        interfaceName = "jakarta.jms.Queue",
        destinationName = "NotificationQueue"
)
public class JmsConfig {
    // This class can be empty, it just holds the JMS destination definition
}
