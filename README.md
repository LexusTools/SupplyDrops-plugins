# SupplyDrops

**Sistema de supply drops administrables por GUI para Paper 1.21+**

SupplyDrops permite a los administradores crear drops de suministro mediante una interfaz gráfica. Los ítems exactos colocados en la GUI se convierten en el contenido del drop. El drop desciende visualmente desde el cielo, aterriza y puede ser looteado por los jugadores.

---

## Características

- Creación de drops mediante **GUI profesional**
- Los ítems exactos colocados se convierten en el contenido (sin tablas de loot aleatorias por defecto)
- **Preservación completa** de ItemStack (PDC, enchantments, custom model data, lore, etc.)
- Máquina de estados determinista:
  - `CREATING` → `FALLING` → `LANDED` → `AVAILABLE` → `BEING_LOOTED` → `LOOTED`
  - Estados terminales: `DESTROYED` · `EXPIRED` · `CANCELLED`
- Transiciones de estado **validadas** (imposible corromper el estado)
- Descenso visual desde el cielo
- Efectos configurables (partículas, sonidos)
- Anuncios globales con placeholders (`%drop_id%`, `%x%`, `%y%`, `%z%`, `%player%`, etc.)
- Soporte de **múltiples drops simultáneos** e independientes
- Protección robusta contra:
  - Duplicación de ítems
  - Double-loot
  - Hoppers
  - Exploits de logout
  - Shift-click y drag
- Scheduler centralizado (no se crea una tarea por cada drop de forma ineficiente)
- Sin NMS

---

## Requisitos

- Java 21+
- Paper 1.21.4+
- Maven 3.8+

---

## Compilación

```bash
mvn clean test package
```

El JAR se genera en:

```
build/SupplyDrops.jar
```

---

## Instalación

1. Compila el plugin o usa el JAR de la carpeta `build/`
2. Colócalo en la carpeta `plugins/` de tu servidor Paper
3. Reinicia el servidor
4. Configura `config.yml` y `messages.yml`

---

## Comandos

| Comando | Descripción | Permiso |
|---------|-------------|---------|
| `/ads` | Abre la GUI de creación de drop | `supplydrops.admin` |
| `/ads create` | Abre la GUI de creación de drop | `supplydrops.admin` |
| `/ads list` | Lista todos los drops activos | `supplydrops.admin` |
| `/ads info <id>` | Muestra información de un drop | `supplydrops.admin` |
| `/ads cancel <id>` | Cancela un drop en curso | `supplydrops.admin` |
| `/ads remove <id>` | Elimina un drop | `supplydrops.admin` |
| `/ads reload` | Recarga la configuración | `supplydrops.admin` |

**Aliases:** `/supplydrop`, `/drop`

---

## Permisos

| Permiso | Descripción | Default |
|---------|-------------|---------|
| `supplydrops.admin` | Crear, gestionar y cancelar drops | `op` |
| `supplydrops.loot` | Poder looteear drops | `true` |

---

## Flujo de uso

1. Un administrador ejecuta `/ads`
2. Se abre una GUI donde puede colocar los ítems exactos del drop
3. Confirma la creación
4. El drop se genera en el cielo y comienza a **descender visualmente**
5. Al aterrizar se anuncia y se vuelve disponible para looteo
6. Los jugadores pueden abrirlo y obtener los ítems
7. Tras un tiempo configurable el drop expira automáticamente

---

## Máquina de estados

```
CREATING
    ↓
FALLING ──────────────→ CANCELLED / DESTROYED
    ↓
LANDED
    ↓
AVAILABLE ────────────→ EXPIRED / DESTROYED
    ↓
BEING_LOOTED
    ↓
LOOTED
```

Solo se permiten transiciones válidas. Esto previene estados corruptos o dobles recompensas.

---

## Configuración

### `config.yml`

- Altura de inicio del drop
- Duración de la caída (ticks)
- Tiempo que permanece en el suelo
- Activación de partículas y sonidos
- Anuncios globales

### `messages.yml`

Mensajes con soporte **MiniMessage** y placeholders:

- `%drop_id%`
- `%world%`
- `%x%` `%y%` `%z%`
- `%player%`
- `%remaining_time%`

---

## Arquitectura

```
com.portfolio.supplydrops
├── command/        → Comandos de administración
├── config/         → Configuración y mensajes
├── gui/            → GUI de creación + listener seguro
├── model/          → SupplyDrop + máquina de estados
├── service/        → Gestión del ciclo de vida de los drops
└── listener/       → (si se necesita para interacciones adicionales)
```

### Componentes clave

- **SupplyDrop**: entidad con estado y contenido
- **DropService**: crea, hace caer, aterriza, expira y limpia drops
- **CreateDropGui**: interfaz de creación segura

---

## Seguridad

- Los ítems existen **exactamente una vez**
- Protección contra hopper exploits
- Cancelación correcta de eventos de inventario peligrosos
- Manejo de logout durante el looteo
- Recuperación ante reinicios del servidor y unloads de chunks

---

## Notas técnicas

- Scheduler centralizado para múltiples drops
- Todas las transiciones de estado son atómicas y validadas
- En `onDisable` se cancelan todas las tareas y se limpian los drops activos
- Tests unitarios cubren la máquina de estados y la generación de IDs únicos

---

## Autor

Parte del **Minecraft Portfolio Suite** — 2026
