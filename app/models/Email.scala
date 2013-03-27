package models

import java.io.InputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import scala.io.Source

case class Email(data: String, x:String) {

}


object Email {
  
  def apply(stream: InputStream) = parse(stream) //new Email(stream, "oi")
  
  
  private def parse(stream: InputStream) = {
	Source.fromInputStream(stream).mkString
  }
      
  
    
  /*public class TransformMessage {

    // Host name used in message identifiers.
    private static final String HOSTNAME = "localhost";

    public static void main(String[] args) throws Exception {
        // Explicitly set a strategy for storing body parts. Usually not
        // necessary; for most applications the default setting is appropriate.
        StorageProvider storageProvider = new TempFileStorageProvider();
        DefaultStorageProvider.setInstance(storageProvider);

        // Create a template message. It would be possible to load a message
        // from an input stream but for this example a message object is created
        // from scratch for demonstration purposes.
        Message template = createTemplate();

        // Create a new message by transforming the template.
        Message transformed = transform(template);

        MessageWriter writer = new DefaultMessageWriter();

        // Print transformed message.
        System.out.println("\n\nTransformed message:\n--------------------\n");
        writer.writeMessage(transformed, System.out);

        // Messages should be disposed of when they are no longer needed.
        // Disposing of a message also disposes of all child elements (e.g. body
        // parts) of the message.
        transformed.dispose();

        // Print original message to illustrate that it was not affected by the
        // transformation.
        System.out.println("\n\nOriginal template:\n------------------\n");
        writer.writeMessage(template, System.out);

        // Original message is no longer needed.
        template.dispose();

        // At this point all temporary files have been deleted because all
        // messages and body parts have been disposed of properly.
    }

    /**
     * Copies the given message and makes some arbitrary changes to the copy.
     * @throws ParseException on bad arguments
     */
    private static Message transform(Message original) throws IOException, ParseException {
        // Create a copy of the template. The copy can be modified without
        // affecting the original.
        MessageBuilder builder = new DefaultMessageBuilder();
        Message message = builder.newMessage(original);

        // In this example we know we have a multipart message. Use
        // Message#isMultipart() if uncertain.
        Multipart multipart = (Multipart) message.getBody();

        // Insert a new text/plain body part after every body part of the
        // template.
        final int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            String text = "Text inserted after part " + (i + 1);
            BodyPart bodyPart = createTextPart(text);
            multipart.addBodyPart(bodyPart, 2 * i + 1);
        }

        // For no particular reason remove the second binary body part (now
        // at index four).
        Entity removed = multipart.removeBodyPart(4);

        // The removed body part no longer has a parent entity it belongs to so
        // it should be disposed of.
        removed.dispose();

        // Set some headers on the transformed message
        message.createMessageId(HOSTNAME);
        message.setSubject("Transformed message");
        message.setDate(new Date());
        message.setFrom(AddressBuilder.DEFAULT.parseMailbox("John Doe <jdoe@machine.example>"));

        return message;
    }

    /**
     * Creates a multipart/mixed message that consists of three parts (one text,
     * two binary).
     */
    private static Message createTemplate() throws IOException {
        Multipart multipart = new MultipartImpl("mixed");

        BodyPart part1 = createTextPart("This is the first part of the template..");
        multipart.addBodyPart(part1);

        BodyPart part2 = createRandomBinaryPart(200);
        multipart.addBodyPart(part2);

        BodyPart part3 = createRandomBinaryPart(300);
        multipart.addBodyPart(part3);

        MessageImpl message = new MessageImpl();
        message.setMultipart(multipart);

        message.setSubject("Template message");

        return message;
    }

    /**
     * Creates a text part from the specified string.
     */
    private static BodyPart createTextPart(String text) {
        TextBody body = new StorageBodyFactory().textBody(text, "UTF-8");

        BodyPart bodyPart = new BodyPart();
        bodyPart.setText(body);
        bodyPart.setContentTransferEncoding("quoted-printable");

        return bodyPart;
    }

    /**
     * Creates a binary part with random content.
     */
    private static BodyPart createRandomBinaryPart(int numberOfBytes)
            throws IOException {
        byte[] data = new byte[numberOfBytes];
        new Random().nextBytes(data);

        Body body = new StorageBodyFactory()
                .binaryBody(new ByteArrayInputStream(data));

        BodyPart bodyPart = new BodyPart();
        bodyPart.setBody(body, "application/octet-stream");
        bodyPart.setContentTransferEncoding("base64");

        return bodyPart;
    }
*/
}