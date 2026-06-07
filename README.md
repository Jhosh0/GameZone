# GameZone - Sistema de Gestión de Videojuegos

Examen Final — Programación de Computadores II (SS300)  
Universidad Popular del César

---

## Integrantes
- Jhosua Andrés Coronel Quintero

---

## Descripción

Sistema de escritorio desarrollado en **JavaFX** para la tienda de videojuegos digitales
**GameZone**. Permite gestionar el catálogo de videojuegos descargables: agregar, listar,
buscar, actualizar y eliminar juegos; realizar ventas, controlar el stock y revisar el
historial de ventas. La persistencia se maneja con archivos en formato **JSON**.

---


## Reglas de Negocio

- **DigitalVideoGame:** si el tamaño supera los **50 GB**, se agregan **$5 000** al precio base.
- **PhysicalVideoGame:** si la condición es `"usado"`, se aplica un **descuento del 25%**.
- Al agregar un videojuego se valida:
    - El título no puede ser nulo ni vacío.
    - El precio debe ser mayor a 0.
    - El stock debe ser mayor o igual a 0.
- No se permiten títulos duplicados (sin distinción de mayúsculas/minúsculas). Si ya existe, se muestra la alerta: *"El videojuego ya existe en el catálogo"*.
- Una venta descuenta el stock y retorna el total (`precio × cantidad`). Si no hay stock suficiente o el juego no existe, se muestra una alerta en la interfaz.

---

## Menú Principal (JavaFX)

```
====================================
  SISTEMA DE GESTIÓN - GAMEZONE
====================================
1. Agregar videojuego   →  CRUD: Crear, Listar, Eliminar, Actualizar
2. Listar todos los videojuegos
3. Buscar por título
4. Buscar por plataforma
5. Realizar venta
6. Mostrar ventas realizadas
7. Salir
====================================
```

---

## Persistencia

El catálogo y el historial de ventas se guardan automáticamente en:

- `data/videogames.json` — catálogo de videojuegos
- `data/sales.json` — historial de ventas realizadas

---

## Cómo Ejecutar

Requiere **JDK 17+** y **Maven**.

```bash
mvn clean javafx:run
```

También puede importarse en **IntelliJ IDEA** como proyecto Maven y ejecutarse desde
la clase principal:

```
com.gamezone.Main
```

---

*Docente: Ing. Esp. Amilkar Hernandez*