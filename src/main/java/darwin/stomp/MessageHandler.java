
package darwin.stomp;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

public class MessageHandler implements MessageListener {
    @Override
    public void onMessage(Message message) {
        String xmlString = convertToXmlString((BytesMessage) message);
        System.out.println(xmlString);
    }

    private String convertToXmlString(BytesMessage bytesMessage) {
        if (bytesMessage != null) try {
            long l = bytesMessage.getBodyLength();
            byte bytesArray[] = new byte[(int) l];
            bytesMessage.readBytes(bytesArray);
            try (Reader streamReader = new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(bytesArray)))) {
                StringBuilder stringBuilder = new StringBuilder();
                char cb[] = new char[1024];
                int s = streamReader.read(cb);
                while (s > -1) {
                    stringBuilder.append(cb, 0, s);
                    s = streamReader.read(cb);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JMSException ex) {
            System.out.println("Failed to parse message");
            ex.printStackTrace();
        }

        return null;
    }
}