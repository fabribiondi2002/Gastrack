package ar.edu.iua.iw3.gastrack.util;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Clase utilitaria para la generacion de contraseñas de activacion
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-25
 */

public final class ContrasenaActivacionUtiles {

    /**
     * Genera una contraseña numérica aleatoria de 5 dígitos.
     *
     * @return Contraseña generada en formato de cadena con ceros a la izquierda si es necesario.
     */
    public static String generarContrasena (){
        int limite = 100000;
        Random random = new Random();
        int num = random.nextInt(limite);
        DecimalFormat decimalFormat = new DecimalFormat("00000");

        return decimalFormat.format(num);
    }
  
  /**
     * Verifica si la contrasena de activacion tiene un formato valido
     * @param contrasena
     * @return true si el formato es valido, false en caso contrario
     */
    public static boolean formatoDeConstrasenaValido(String contrasena)
    {
        //Verificar si la cadena es nula o vacía
        if (contrasena == null || contrasena.isEmpty()) {
            return false;
        }

        //Verificar que la longitud sea exactamente 5
        if (contrasena.length() != 5) {
            return false;
        }

        //Verificar que todos los caracteres sean dígitos
        for (char c : contrasena.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }


        return true;   
    }

}
