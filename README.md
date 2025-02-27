
# Servicio de Consulta de Precios – Prueba Técnica Inditex

## Introdución
Esta aplicación es un servicio RESTful desarrollado en Spring Boot que permite consultar el precio aplicable para un producto y una marca específicos en una fecha determinada. El objetivo es cumplir con los requerimientos de la prueba técnica, devolviendo un único precio aplicable según los parámetros de entrada (fecha de aplicación, identificador de producto e identificador de cadena).
## Tecnologías Utilizadas

- **Spring Boot 3.4.3**: Framework principal para el desarrollo de la aplicación, ofreciendo configuraciones predeterminadas y facilitando la creación de servicios REST.

- **Java 21**: Versión del lenguaje utilizada, aprovechando mejoras de rendimiento y nuevas características.

- **H2 Database**: Base de datos en memoria para almacenar los datos de precios.
- **Spring Data JPA**: Para la interacción con la base de datos, proporcionando una capa de abstracción sobre Hibernate.
- **Lombok**: Para reducir el código boilerplate en entidades y servicios, mejorando la legibilidad.
- **Spring Cache con Caffeine**: Para caching de resultados, optimizando el rendimiento en consultas repetidas.
- **SpringDoc OpenAPI**: Para generar documentación automática de la API REST, accesible en /swagger-ui.html.
- **Jacoco**: Para medir la cobertura de código en los tests.
- **Mockito y JUnit 5**: Para la creación de tests unitarios y de integración.

## Justificación de las Tecnologías

- **Java 21**: Proporciona mejoras de rendimiento y nuevas funcionalidades, asegurando que el código esté alineado con estándares modernos. En este proyecto, se utiliza la característica de `records` introducida en Java 16, que permite definir clases inmutables de manera concisa, como en `PriceResponse`. Si se usara Java 11, no se podría emplear `records`, lo que obligaría a definir clases con campos finales, constructores, getters, etc., aumentando el código boilerplate y reduciendo la legibilidad. Además, Java 21 incluye mejoras en el recolector de basura y en el rendimiento general, aunque en un proyecto pequeño como este el impacto es limitado.
- **H2**: Perfecta para entornos de desarrollo y pruebas, ya que no requiere infraestructura externa y es ligera.
- **Spring Data JPA**: Simplifica el acceso a datos y reduce el código repetitivo, alineándose con el principio DRY (Don't Repeat Yourself).
- **Lombok**: Mejora la legibilidad y mantenibilidad al eliminar getters, setters y constructores manuales.
- **Caffeine**: Una librería de caching de alto rendimiento, ideal para optimizar consultas frecuentes en un entorno con alta concurrencia.
- **SpringDoc OpenAPI**: Facilita la documentación automática de la API, mejorando la usabilidad para otros desarrolladores.
- **Jacoco**: Herramienta estándar para medir la cobertura de tests, garantizando la calidad del código.
- **Mockito y JUnit 5**: Frameworks ampliamente adoptados que permiten escribir tests robustos, esenciales para validar el comportamiento del sistema.
##  Arquitectura

La aplicación utiliza una **arquitectura hexagonal** (ports and adapters), que separa la lógica de negocio del acceso a datos y las interfaces externas. Aseguramos así:
- **Desacoplamiento**: Las capas están aisladas mediante interfaces (puertos).
- **Testabilidad**: Facilita el uso de mocks para probar cada componente independientemente.
- **Flexibilidad**: Permite cambiar implementaciones (como la base de datos o el mecanismo de caching) sin afectar la lógica de negocio.

### Capas Principales:
1. **Domain**: Contiene el modelo de negocio (`Price`) y las interfaces de los puertos (`PriceRepositoryPort`). Es el núcleo de la aplicación y no depende de ninguna tecnología externa.
2. **Application**: Implementa la lógica de negocio en `PriceServiceImpl`, orquestando las interacciones entre el dominio y la infraestructura.
3. **Adapters**: Incluye el controlador REST (`PriceController`) y el adaptador de persistencia (`PriceRepositoryAdapter`), que conectan la lógica de negocio con el mundo exterior.
4. **Infrastructure**: Configuraciones específicas como caching (`CacheConfig`) y persistencia JPA (`PriceRepositoryJpa`).

---
## Estructura del Código
La estructura de paquetes sigue la arquitectura hexagonal y está organizada bajo `es.test.inditex`:

- **`adapter.rest`**: Contiene el controlador (`PriceController`), DTOs (`PriceResponse`) y la gestión de excepciones (`GlobalExceptionHandler`).
- **`application.service`**: Define el servicio (`PriceService`) y su implementación (`PriceServiceImpl`), que coordina la lógica de negocio.
- **`domain`**: Incluye el modelo (`Price`), excepciones (`PriceNotFoundException`) y puertos (`PriceRepositoryPort`).
- **`infrastructure`**: Configuraciones (`CacheConfig`) y persistencia JPA (`PriceEntity`, `PriceRepositoryJpa`, `PriceRepositoryAdapter`).
- **`resources`**: Archivos de configuración (`application.properties`) y scripts SQL (`schema.sql`, `data.sql`) para inicializar la base de datos.

Se ha procurado que cada capa tenga una responsabilidad única, cumpliendo con el principio de **Single Responsibility** de SOLID.

---

## Justificación de Diseño: Tipos de Datos y Validaciones
Esta sección detalla las decisiones de diseño tomadas en la entidad `PriceEntity` y en el controlador `PriceController`, explicando la elección de tipos de datos y validadores.

---

## Tipos de Datos en la Entidad `PriceEntity`

En el diseño de la entidad `PriceEntity`, cada campo fue seleccionado basándose en la naturaleza de los datos, A continuación, se explica la elección de cada tipo:

### `id` (Long)
- **Clave primaria** generada automáticamente con `GenerationType.IDENTITY`.
- **Razones:**
  - Permite un rango amplio de valores, ideal para grandes volúmenes de registros.
  - Es eficiente en términos de almacenamiento (8 bytes) y en el rendimiento de las consultas.
  - Es un estándar común para claves primarias en bases de datos relacionales como H2.

### `brandId` (Long)
- Representa una **clave foránea** que referencia la tabla de marcas (por ejemplo, 1 = ZARA).
- **Razones:**
  - Mantiene consistencia con el tipo de dato habitual en claves primarias de tablas relacionadas.

### `startDate` y `endDate` (LocalDateTime)
- Definen el **rango de fechas y horas** en que un precio es aplicable.
- **Razones:**
  - Almacenan fecha y hora sin zona horaria, lo cual es adecuado cuando se asume una zona común.
  - Forman parte de la API moderna de Java (desde Java 8) que es más robusta y fácil de manipular que `Date` o `Calendar`.
  - Permiten comparaciones precisas para determinar si una fecha de consulta cae dentro del rango.
  - **Nota:** Aunque se podría haber utilizado **`Instant`** para representar un momento exacto en tiempo UTC, se optó por `LocalDateTime` ya que he supuesto que la aplicación opera en un contexto de zona horaria fija y no requiere la conversión a tiempos universales, lo que simplifica la lógica y la comprensión del código.

### `priceList` (Integer)
- Identifica la **tarifa de precios** aplicable.
- **Razones:**
  - Los identificadores de tarifas son números enteros pequeños y no se espera que excedan el rango de `Integer`.
  - Es eficiente en almacenamiento (4 bytes)

### `productId` (Long)
- Es una **clave foránea** que referencia la tabla de productos.
- **Razones:**
  - Asegura consistencia con las claves primarias de la tabla de productos.

### `priority` (Integer)
- Determina qué tarifa se aplica en caso de solapamiento de rangos.
- **Razones:**
  - Las prioridades son valores enteros pequeños; el rango de `Integer` es suficiente.
  - Facilita comparaciones y ordenaciones en la consulta JPA (por ejemplo, `ORDER BY p.priority DESC`).

### `price` (BigDecimal)
- Representa el **precio final de venta**.
- **Razones:**
  - Es el tipo recomendado para valores monetarios, ya que ofrece precisión decimal exacta.
  - Evita errores de redondeo que podrían ocurrir con tipos `float` o `double`.
  - Es esencial para cálculos financieros precisos en sistemas de comercio electrónico.

### `curr` (String)
- Almacena el **código ISO de la moneda** (por ejemplo, "EUR").
- **Razones:**
  - Los códigos de moneda son cadenas cortas de texto (generalmente 3 caracteres).

---

## Validadores en el Controlador (`PriceController`)

En el controlador se aplicaron las anotaciones de validación `@NotNull` y `@Min` a los parámetros de entrada `productId` y `brandId` para asegurar la integridad de la solicitud:

### `@NotNull`
- **Razón:**
  - Los parámetros `productId` y `brandId` son esenciales para identificar el producto y la marca.
  - Sin estos valores, la solicitud no tendría sentido y no podría procesarse correctamente.
- **Beneficio:**
  - Evita que se procesen solicitudes incompletas.
  - Retorna un error `400 Bad Request` de forma inmediata si faltan estos parámetros, previniendo comportamientos inesperados en etapas posteriores.

### `@Min(value = 1)`
- **Razón:**
  - Se establece que los identificadores deben ser mayores o iguales a 1, ya que en bases de datos relacionales las claves primarias suelen comenzar en 1.
  - Valores de 0 o negativos serían inválidos o inesperados en este contexto.
- **Beneficio:**
  - Evita que solicitudes con identificadores inválidos lleguen a la capa de servicio o a la base de datos.
  - Mejora la eficiencia del sistema al evitar procesamiento innecesario.

---

### Eficiencia
- **Principio "Fail Fast":**  
  - Las validaciones en el controlador permiten detectar y rechazar solicitudes inválidas lo antes posible.
  - Si `productId` o `brandId` son nulos o menores a 1, se retorna un error sin necesidad de consultar la base de datos, ahorrando recursos del servidor.

### Robustez
- **Integridad de Datos:**
  - Las anotaciones `@NotNull` y `@Min` garantizan que solo se procesen solicitudes bien formadas.
  - Se reduce la posibilidad de errores en tiempo de ejecución, como `NullPointerException` o consultas fallidas.

---
## Implementación de Funcionalidades Clave
### Selección del Precio Correcto
- La lógica en `PriceServiceImpl` utiliza el método `findTopPrice` del repositorio para buscar el precio con mayor prioridad dentro del rango de fechas especificado.
- **Consulta JPA Elegida**: Se optó por una consulta JPQL personalizada en `PriceRepositoryJpa` para buscar el precio con la mayor prioridad dentro del rango de fechas. Este enfoque ofrece una consulta eficiente, legible y bien integrada con las capacidades de JPA para manejar filtrado y ordenación (`ORDER BY p.priority DESC LIMIT 1`), asegurando que siempre se devuelva un único precio como requiere la prueba.

- **Capa de Dominio**
    - La entidad `Price` en la capa de dominio es anémica, es decir, solo contiene datos sin lógica de negocio. Esto se debe a la simplicidad de la prueba técnica, que se centra en una consulta básica. La lógica de selección del precio está implementada en el servicio `PriceServiceImpl`, lo cual es una práctica común en proyectos con requerimientos no complejos. En un contexto más amplio, se podría enriquecer la entidad con métodos siguiendo Domain-Driven Design (DDD). Por ejemplo:
```java
public boolean isApplicable(LocalDateTime date) {
    return date.isAfter(startDate) && date.isBefore(endDate);
}

public boolean hasHigherPriority(Price other) {
    return this.priority > other.priority;
}
```

- **Alternativas Consideradas**:
  - **Nomenclaturas de Métodos de JPA**: Spring Data JPA permite definir consultas mediante nombres de métodos, como `findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc`. Aunque funcional, este método genera nombres largos y poco mantenibles, especialmente con múltiples condiciones, lo que va en contra de los principios de Clean Code.
  - **Consulta Nativa (Native Query)**: Usar SQL nativo podría ofrecer un ligero aumento de rendimiento al evitar la abstracción de JPA. Sin embargo, sacrificaría la portabilidad (por ejemplo, al cambiar de H2 a otra base de datos) y la facilidad de mantenimiento, ya que el código sería más dependiente de la estructura específica de la base de datos.

- **Razón de la Elección**: La consulta JPQL personalizada equilibra legibilidad, mantenibilidad y rendimiento. Al estar encapsulada en el repositorio, es fácil de testear y modificar si los requerimientos cambian, cumpliendo con los principios SOLID (Single Responsibility y Open/Closed) y facilitando consultas eficientes en grandes volúmenes de datos gracias al índice definido.
- La consulta JPA está optimizada con un índice (`idx_prices_product_brand_dates_priority`) para mejorar el rendimiento en grandes volúmenes de datos.

### Caching
- Se implementa Spring Cache con Caffeine en `PriceServiceImpl` usando la anotación `@Cacheable`. La clave de cache combina `productId`, `brandId` y `date`, reduciendo consultas repetitivas a la base de datos.
- Configuración: tamaño máximo de 500 entradas, expiración tras 10 minutos de inactividad.
- **Configuración del Cache**:
  - **Tamaño Máximo**: Se estableció en 500 entradas para limitar el uso de memoria, asumiendo un volumen moderado de combinaciones únicas de `productId`, `brandId` y `date`. Este valor es un placeholder conservador que evita el consumo excesivo de recursos en un entorno de prueba.
  - **Expiración**: Las entradas expiran tras 10 minutos de inactividad, permitiendo mantener los datos frescos y liberar memoria de caches no utilizados recientemente. Este tiempo es un punto de partida razonable para un sistema con consultas esporádicas.
  - **Valores Elegidos**: Estos parámetros son valores por defecto para la prueba técnica. En un escenario real, se deberían ajustar mediante análisis de patrones de uso (frecuencia de consultas, tamaño del catálogo de productos) y pruebas de rendimiento, pudiendo aumentar el tamaño o reducir el tiempo de expiración según las necesidades del sistema.

- **Implicaciones de Cambios en la Base de Datos**:
  - Actualmente, el cache asume que los datos en la base de datos son estáticos, como se indica en la prueba técnica (datos de ejemplo fijos). Si se añaden, eliminan o modifican precios, el cache podría devolver datos obsoletos hasta que las entradas expiren o se invaliden manualmente.
  - **Soluciones Posibles**: 
    - **Invalidación de Cache**: Implementar lógica para invalidar entradas específicas (usando `@CacheEvict`) cuando se actualicen precios, asegurando coherencia entre el cache y la base de datos.
    - **TTL (Time To Live)**: Configurar un tiempo de vida fijo más corto para forzar refrescos periódicos, adecuado si los datos cambian con frecuencia.
    - **Cache Aside**: Consultar siempre la base de datos primero y actualizar el cache solo con datos frescos, aunque esto reduciría los beneficios de rendimiento del caching.
  - En este proyecto, dado que los datos son estáticos y el enfoque es una prueba técnica, no se implementaron estas estrategias. Sin embargo, en producción serían esenciales para garantizar datos actualizados.

### Gestión de Excepciones
- **`GlobalExceptionHandler`** captura excepciones específicas:
  - `PriceNotFoundException`: Devuelve `404 Not Found`.
  - Errores de validación (`ConstraintViolationException`, `MissingServletRequestParameterException`): Devuelve `400 Bad Request`.
  - Excepciones genéricas: Devuelve `500 Internal Server Error`.
- Se utilizan códigos HTTP específicos para proporcionar respuestas claras al cliente.
- **Elección de Códigos HTTP**:
  - **`404 Not Found`**: Se usa para `PriceNotFoundException`, indicando que no existe un precio para los parámetros dados. Es el estándar REST para recursos inexistentes.
  - **`400 Bad Request`**: Aplicado a errores de validación (como parámetros faltantes o inválidos), reflejando que la solicitud del cliente está malformada y no puede procesarse.
  - **`500 Internal Server Error`**: Reservado para excepciones no controladas, señalando un fallo inesperado en el servidor que el cliente no puede resolver.

- **Personalización en un Escenario Real**:
  - En producción, los códigos HTTP por sí solos podrían no ser suficientes para consumidores de la API que necesiten información detallada. Se podría estandarizar las respuestas de error incluyendo un cuerpo JSON con campos como `errorCode` (un identificador único del error), `message` (descripción legible) y `details` (información adicional para depuración). Ejemplo:
    ```json
    {
      "errorCode": "PRICE_NOT_FOUND",
      "message": "No se encontró precio para los parámetros indicados.",
      "details": "Verifique los valores de date, productId y brandId."
    }
    ```
  - Esta estructura mejora la experiencia del cliente al permitir un manejo granular de errores y facilita la depuración. Además, los códigos HTTP podrían ajustarse según estándares internos de la empresa (por ejemplo, usar `422 Unprocessable Entity` para validaciones específicas), siempre que se documente claramente en la API.

---
## Base de Datos

- **H2**: Configurada como base de datos en memoria (`jdbc:h2:mem:inditexdb`), inicializada con `schema.sql` (creación de tablas e índices) y `data.sql` (datos de ejemplo proporcionados en la prueba).
- **Tabla PRICES**: Mapeada a la entidad `PriceEntity` con campos como `brandId`, `startDate`, `endDate`, `priceList`, `productId`, `priority`, `price` y `curr`.
- **Índices**: Un índice compuesto asegura consultas rápidas basadas en `productId`, `brandId`, `startDate`, `endDate` y `priority`.

---
## Instrucciones de Uso

### Requisitos
- Java 21
- Maven 3.8+

### Ejecución Local
1. Clonar el repositorio: `git clone [<URL>](https://github.com/dgo95/test-inditex.git)`.
2. Navegar al directorio: `cd test-inditex`.
3. Compilar y ejecutar: `mvn spring-boot:run`.
4. Acceder al endpoint: `GET http://localhost:8080/api/prices?date=2020-06-14T10:00:00&productId=35455&brandId=1`.

### Documentación de la API
- Disponible en: `http://localhost:8080/swagger-ui.html`.

### Ejecución de Tests
1. Ejecutar: `mvn test`.
2. Generar reporte de cobertura: `mvn jacoco:report`.
3. Ver reporte en: `target/site/jacoco/index.html`.

---
## Contenerización

La aplicación se ha containerizado utilizando Docker, lo que garantiza portabilidad, consistencia y facilidad de despliegue en cualquier entorno (desarrollo, pruebas o producción). Gracias a esta estrategia, la aplicación se ejecuta en un entorno aislado con todas sus dependencias, eliminando problemas de configuración en el sistema operativo host.

### Estrategia Multi-etapa en el Dockerfile

Se ha implementado un **Dockerfile multi-etapa** que divide el proceso en dos fases principales:

#### 1. Etapa de Compilación (Build Stage)

- **Imagen Base:**  
  `maven:3.9.6-eclipse-temurin-21`

- **Pasos Realizados:**
  - Se establece el directorio de trabajo:
    ```dockerfile
    WORKDIR /app
    ```
  - Se copia el archivo `pom.xml` para aprovechar el cacheo de capas y descargar las dependencias:
    ```dockerfile
    COPY pom.xml .
    RUN mvn dependency:go-offline
    ```
  - Se copia el código fuente y se compila la aplicación (omitiendo los tests):
    ```dockerfile
    COPY src ./src
    RUN mvn clean package -DskipTests
    ```

#### 2. Etapa de Ejecución (Runtime Stage)

- **Imagen Base:**  
  `eclipse-temurin:21-jre-jammy`

- **Pasos Realizados:**
  - Se establece el directorio de trabajo:
    ```dockerfile
    WORKDIR /app
    ```
  - Se copia el JAR generado en la etapa de compilación:
    ```dockerfile
    COPY --from=build /app/target/inditex-0.0.1-SNAPSHOT.jar app.jar
    ```
  - Se expone el puerto 8080:
    ```dockerfile
    EXPOSE 8080
    ```
  - Se define el comando de entrada para iniciar la aplicación:
    ```dockerfile
    ENTRYPOINT ["java", "-jar", "app.jar"]
    ```

### Construcción y Ejecución de la Imagen

1. **Construir la Imagen Docker:**

   Ejecuta el siguiente comando en la raíz del proyecto (donde se encuentra el Dockerfile):

   ```bash
       docker build -t inditex-app .
    ```
2. Ejecutar el Contenedor:

Inicia un contenedor que mapee el puerto 8080:

```bash
docker run -p 8080:8080 inditex-app
```
La aplicación estará disponible en http://localhost:8080.
