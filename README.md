# 🚚 Gastrack

Proyecto para la materia IW3. Gastrack es un sistema backend desarrollado en Java con Spring Boot para la administración y trazabilidad de órdenes de carga de gas líquido. El objetivo es integrar datos en tiempo real (API), comunicarse con sistemas externos (por ejemplo SAP y TMS), persistir la información y ofrecer endpoints para gestionar todo el ciclo de una orden: desde la recepción, pesajes, carga en tiempo real y conciliación final.

## 🧩 Descripción del dominio
El sistema gestiona órdenes de carga que reciben datos iniciales desde sistemas externos. Cada orden pasa por estados secuenciales:
1. Pendiente de pesaje inicial (creada a partir de datos externos).
2. Con pesaje inicial registrado (tara + contraseña de activación).
3. Cerrada para carga (se bloquea recepción de datos de detalle).
4. Finalizada (pesaje final recibido y conciliación generada).

Datos relevantes manejados:
- Orden: número, camión, chofer, cliente, producto, preset, fechas (creación, tara, inicio/fin carga, pesaje final).
- Camión: patente, cisternado (volúmenes).
- Chofer: nombre, apellido, documento.
- Cliente: razón social, contacto.
- Producto: nombre, descripción.
- Datos de detalle (en tiempo real): masa acumulada (kg), densidad, temperatura (°C), caudal (kg/h).  
  - Se conservan el último valor válido en la cabecera y registros históricos según criterio de frecuencia/validez.  
  - Se descartan registros inválidos (caudal ≤ 0, masa acumulada ≤ 0 o menor que anterior).

Además, el sistema debe:
- Generar y devolver el preset para iniciar la carga tras validar orden y contraseña.
- Recibir datos de caudalímetro másico y almacenar por frecuencia configurada.
- Ofrecer endpoint para cierre de orden y endpoint para recibir pesaje final y retornar conciliación con neto por balanza, diferencia, promedios, etc.

## 🛠️ Tecnología y dependencias
- Lenguaje: Java (recomendado JDK 11+)
- Framework: Spring Boot
- Gestión de dependencias: Maven (pom.xml)


## 📚 Documentación 

- Documentación OpenAPI / Swagger:
  - Ruta típica (al ejecutar la app): 
    - Interfaz Swagger UI: /swagger-ui.html o /swagger-ui/index.html
    - Especificación raw (JSON): /v3/api-docs

## ✅ Alcance actual (Segundo parcial)
Estado: Segundo parcial (entrega intermedia). A continuación se indica lo implementado y lo pendiente para el final.

Completado (incluye lo exigido para el segundo parcial):
- Implementación de los endpoints REST principales para los puntos 1 a 5 (recepción de orden, registro de tara, ingestión de datos de detalle, cierre de orden, registro de pesaje final y conciliación). ✅
- Documentación técnica de las APIs con OpenAPI (Swagger) disponible. ✅
- API implementada sin interfaz gráfica (cumple la entrega del segundo parcial). ✅
- Implementación de la parametrización de frecuencia de almacenamiento de detalle y políticas de muestreo. ✅
- Cálculo de promedios (temperatura, densidad, caudal) en conciliación implementado. ✅

Pendiente (próximos pasos para examen final):
- Seguridad y roles de acceso (JWT / Spring Security). 🔐
- Alarmas de temperatura configurables y envío de correo, con mecanismo de aceptación para evitar reenvíos. 📧🔥
- Frontend web para monitoreo en tiempo real y administración (UI con autenticación, filtros, vistas de órdenes, aceptación de alarmas). 🖥️
- Logs/auditoría de cambios de estado con usuario/proceso y observaciones. 🧾

## 🤝 Cómo contribuir
1. Haz fork del repositorio.  
2. Crea una rama para tu cambio:
   ```bash
   git checkout -b feat/nombre-de-la-funcionalidad
   ```
3. Realiza tus cambios y agrega pruebas si corresponde.  
4. Haz commit y push:
   ```bash
   git add .
   git commit -m "Descripción corta del cambio"
   git push origin feat/nombre-de-la-funcionalidad
   ```
5. Abre un Pull Request describiendo los cambios realizados.

## 👥 Autores / Contacto
- lbiondi733@alumnos.iua.edu.ar — Biondi Fabricio  
- bvargas161@alumnos.iua.edu.ar — Vargas Benjamin  
- cbadami845@alumnos.iua.edu.ar — Antonella Badami

---
