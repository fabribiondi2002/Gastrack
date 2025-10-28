package ar.edu.iua.iw3.gastrack.util;

public final class ContrasenaActivacionUtiles {

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
