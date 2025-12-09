package ar.edu.iua.iw3.gastrack.util;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import ar.edu.iua.iw3.gastrack.model.business.exception.BusinessException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailBusiness {
	@Autowired
	private JavaMailSender emailSender;


	public void sendSimpleMessage(String to, String subject, String text) throws BusinessException {
		log.trace("Enviando mail subject={} a: {}", subject, to);
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("noreply@magm.com.ar");
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			emailSender.send(message);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
	}


	public void sendHighTempAlert(
		List<String> contacts,
    	String asunto,
    	String detalle,
    	String orden,
    	String patente,
    	String fecha,
    	String link
    ) {
        try {

            ClassPathResource resource = new ClassPathResource("templates/HighTempAlert.html");
            String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

			html = html.replace("${detalle}", detalle == null ? "" : detalle);
    		html = html.replace("${orden}", orden == null ? "" : orden);
    		html = html.replace("${patente}", patente == null ? "" : patente);
    		html = html.replace("${fecha}", fecha == null ? "" : fecha);
    		html = html.replace("${link}", link == null ? "#" : link);


            MimeMessage mensaje = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
 
            helper.setTo(contacts.toArray(new String[0])); 
            helper.setSubject(asunto);
            helper.setText(html, true);

            emailSender.send(mensaje);
			log.info("Mail de alerta de alta temperatura enviado a: {}", String.join(", ", contacts));

        } catch (Exception e) {
			log.warn("Mail de alerta de alta temperatura falló: " + e.getMessage());
            throw new RuntimeException("Error al enviar mail: " + e.getMessage());

        }
    }
}

