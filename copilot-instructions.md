# GitHub Copilot Instructions for Patient Care System

## Project Overview
This is a Spring Boot 3.2.0 healthcare management system that manages patients, progress notes, care plans, and appointments. The system exposes REST APIs and MCP server endpoints.

## Technology Stack
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- Lombok for reducing boilerplate
- Jackson for JSON serialization
- Java 17

## Project Structure Standards

### Package Organization
```
com.healthcare.patientcare/
├── entity/          # JPA entities
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic layer
├── controller/      # REST controllers
├── mcp/            # MCP server components
│   ├── annotation/ # Custom annotations
│   ├── controller/ # MCP endpoints
│   └── service/    # MCP tools
└── config/         # Configuration classes
```

## Naming Conventions

### Entities
- Singular noun: `Patient`, `Appointment`, `CarePlan`
- Table name: plural lowercase with underscore: `@Table(name = "appointments")`
- Always include audit fields when appropriate

### DTOs (if needed)
- Request: `{Entity}RequestDTO` (e.g., `AppointmentRequestDTO`)
- Response: `{Entity}ResponseDTO` (e.g., `AppointmentResponseDTO`)
- Place in: `com.healthcare.patientcare.dto.request` and `.dto.response`

### Services
- Interface: `{Entity}Service` (optional for simple services)
- Implementation: Direct class `{Entity}Service`
- Annotation: `@Service` and `@Transactional`

### Controllers
- Name: `{Entity}Controller`
- Base path: `/api/{resource-plural}` (e.g., `/api/appointments`)
- Annotation: `@RestController` and `@RequestMapping`

### Repositories
- Name: `{Entity}Repository`
- Extend: `JpaRepository<Entity, Long>`
- Annotation: `@Repository`

## Entity Standards

### Required Structure
```java
@Entity
@Table(name = "table_name")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityName {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Entity-specific fields
    
    // Relationships with appropriate annotations
    // Use @JsonIgnore on the owning side to prevent circular references
}
```

### Common Patterns
- Use `@Column(nullable = false)` for required fields
- String lengths: `@Column(length = X)` for large text
- Dates: Use `LocalDate` for dates, `LocalDateTime` for timestamps
- Relationships:
  - `@ManyToOne`: Use `@JoinColumn(name = "foreign_key_id", nullable = false)`
  - `@OneToMany`: Use `mappedBy`, `cascade = CascadeType.ALL`, `orphanRemoval = true`
  - Add `@JsonIgnore` on child entity's parent reference to avoid circular JSON

### Example Relationship Pattern
```java
// Parent side
@OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Appointment> appointments = new ArrayList<>();

// Child side
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "patient_id", nullable = false)
@JsonIgnore
private Patient patient;
```

## Repository Standards

### Custom Query Methods
- Follow Spring Data JPA naming conventions
- Examples:
  - `findByPatientId(Long patientId)`
  - `findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end)`
  - `findByStatusAndPatientId(String status, Long patientId)`

### When to Use @Query
- Complex queries with multiple joins
- Native queries for database-specific operations
- Custom projections

## Service Layer Standards

### Structure
```java
@Service
@Transactional
public class EntityService {
    
    @Autowired
    private EntityRepository entityRepository;
    
    @Autowired
    private RelatedRepository relatedRepository;
    
    // Public methods with clear business logic
    // Throw RuntimeException with descriptive messages for errors
}
```

### Error Handling
- Throw `RuntimeException` with clear messages: `"Patient not found with id: " + id`
- Format: `"{Entity} not found with {field}: {value}"`
- Let controller layer handle HTTP status codes

### Transaction Management
- Use `@Transactional` at class level
- Read-only methods can use `@Transactional(readOnly = true)` (optional)

## Controller Standards

### Structure
```java
@RestController
@RequestMapping("/api/{resource-plural}")
public class EntityController {
    
    @Autowired
    private EntityService entityService;
    
    // CRUD endpoints
}
```

### Standard Endpoints Pattern
1. **Create**: `POST /` - Returns `ResponseEntity<Entity>`
2. **Get by ID**: `GET /{id}` - Returns `ResponseEntity<Entity>`
3. **Get All**: `GET /` - Returns `ResponseEntity<List<Entity>>`
4. **Update**: `PUT /{id}` - Returns `ResponseEntity<Entity>`
5. **Delete**: `DELETE /{id}` - Returns `ResponseEntity<Void>`
6. **Custom queries**: `GET /by-{field}/{value}`

### Response Handling
- Success: `ResponseEntity.ok(data)`
- Not Found: `ResponseEntity.notFound().build()`
- Created: `ResponseEntity.ok(created)` (simplified, not using 201)
- Error: Let exception handler manage (or return appropriate status)

### Example Controller Method
```java
@GetMapping("/{id}")
public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
    return appointmentService.getAppointmentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PostMapping
public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
    Appointment created = appointmentService.createAppointment(appointment);
    return ResponseEntity.ok(created);
}
```

## MCP Tool Standards

### Tool Service Location
- Create methods in `com.healthcare.patientcare.mcp.service.MCPToolService`
- All MCP tools should be in this single service class

### Tool Annotation Pattern
```java
@Tool(
    name = "tool_name",
    description = "Clear description of what this tool does"
)
public String methodName(
    @ToolParam(name = "param_name", description = "Parameter description") Type paramName
) {
    try {
        // Call service layer
        // Convert result to JSON string
        return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
        return "{\"error\": \"" + e.getMessage() + "\"}";
    }
}
```

### Tool Naming
- Use snake_case: `schedule_appointment`, `get_appointments`, `cancel_appointment`
- Be descriptive and action-oriented

### Tool Parameters
- All parameters use `@ToolParam` annotation
- Mark required with `required = true` (default)
- Use appropriate Java types: `Long` for IDs, `String` for text, `LocalDate` for dates

### Return Format
- Always return JSON string
- Success: Serialized entity or list
- Error: `{"error": "Error message"}`
- Use try-catch to handle exceptions gracefully

## Database Configuration

### H2 Database
- In-memory database configured in `application.properties`
- Auto-creates schema on startup: `spring.jpa.hibernate.ddl-auto=create-drop`
- Sample data loaded via `DataInitializer` class

### Data Initialization Pattern
```java
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private EntityRepository entityRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Create sample data
        // Use realistic values
    }
}
```

## Code Quality Standards

### Use Lombok
- `@Data` for entities (generates getters, setters, toString, equals, hashCode)
- `@NoArgsConstructor` and `@AllArgsConstructor` for constructors
- Keep entities simple - no business logic

### Dependency Injection
- Use `@Autowired` on fields (project convention)
- Alternative: Constructor injection (more testable, but use field for consistency)

### Logging
- Not currently used in project, but can add SLF4J if needed
- Example: `log.info("Creating appointment for patient: {}", patientId);`

### Comments
- Javadoc on public service methods (optional)
- Inline comments only for complex logic
- Code should be self-documenting with clear method names

## Testing Standards (Future)

### Integration Tests
- Use `@SpringBootTest`
- Test complete flows: Controller → Service → Repository
- Use H2 in-memory database for tests

### Repository Tests
- Use `@DataJpaTest`
- Test custom query methods

## Common Patterns in This Project

### Pattern 1: Creating Related Entities
```java
// In Service
public Appointment createAppointment(Long patientId, Appointment appointment) {
    Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
    
    appointment.setPatient(patient);
    return appointmentRepository.save(appointment);
}
```

### Pattern 2: Querying Related Data
```java
// In Controller
@GetMapping("/{patientId}/appointments")
public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable Long patientId) {
    List<Appointment> appointments = appointmentService.getAppointmentsByPatientId(patientId);
    return ResponseEntity.ok(appointments);
}
```

### Pattern 3: MCP Tool for Entity Creation
```java
@Tool(name = "create_entity", description = "Create a new entity")
public String createEntity(
    @ToolParam(name = "field1", description = "Description") String field1,
    @ToolParam(name = "related_id", description = "ID of related entity") Long relatedId
) {
    try {
        Entity entity = new Entity();
        entity.setField1(field1);
        // Set other fields
        
        Entity created = entityService.createEntity(relatedId, entity);
        return objectMapper.writeValueAsString(created);
    } catch (Exception e) {
        return "{\"error\": \"" + e.getMessage() + "\"}";
    }
}
```

## Specific Instructions for New Features

When adding a new feature (e.g., Appointments):

1. **Start with Entity**: Create the JPA entity with all necessary fields and relationships
2. **Create Repository**: Extend JpaRepository with custom query methods
3. **Build Service**: Implement business logic with proper error handling
4. **Add Controller**: Create REST endpoints following the standard pattern
5. **Add MCP Tools**: Create @Tool annotated methods for common operations
6. **Initialize Data**: Add sample data in DataInitializer
7. **Update Related Entities**: Add relationships to existing entities if needed

## Important Reminders

- Always use `@JsonIgnore` on child entity references to parent to prevent circular JSON
- Initialize collections in entities: `= new ArrayList<>()`
- Use `Optional` return types for single entity queries
- Use `List` return types for collections
- Always validate required relationships before saving
- Keep controller thin - business logic belongs in service layer
- MCP tools should call service methods, not repositories directly
- Use meaningful variable names - avoid abbreviations
- Format dates consistently: ISO 8601 format (yyyy-MM-dd for dates, yyyy-MM-dd'T'HH:mm:ss for timestamps)

## Current Entities Overview

### Patient
- Primary entity representing a patient
- Has relationships: `List<ProgressNote>`, `CarePlan`, `List<Appointment>` (to be added)
- Fields: name, patientId, dateOfBirth, gender, contactNumber, email, address

### ProgressNote
- Clinical notes about patient progress
- Relationship: `@ManyToOne` to Patient
- Fields: note, dateTime, provider, noteType

### CarePlan
- Treatment plan for a patient
- Relationship: `@OneToOne` to Patient
- Fields: goals, interventions, medications, startDate, reviewDate, status

### Appointment (to be created)
- Should have: appointmentDate, appointmentTime, reason, status, provider
- Relationship: `@ManyToOne` to Patient
- Status values: "Scheduled", "Completed", "Cancelled", "No-Show"

## Example: Full Feature Implementation

See existing features (Patient, ProgressNote, CarePlan) as reference for:
- Entity design with relationships
- Repository custom methods
- Service implementation
- Controller REST endpoints
- MCP tool integration
- Sample data initialization

Follow these patterns exactly for consistency across the codebase.