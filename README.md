# Patient Care System with Spring-based MCP Server

A comprehensive healthcare management system built with Spring Boot that exposes patient data, progress notes, and care plans through REST APIs and a native Spring MCP server.

## Project Structure

```
patient-care-system/
├── src/main/java/com/healthcare/patientcare/
│   ├── PatientCareApplication.java
│   ├── entity/
│   │   ├── Patient.java
│   │   ├── ProgressNote.java
│   │   └── CarePlan.java
│   ├── repository/
│   │   ├── PatientRepository.java
│   │   ├── ProgressNoteRepository.java
│   │   └── CarePlanRepository.java
│   ├── service/
│   │   └── PatientService.java
│   ├── controller/
│   │   └── PatientController.java
│   ├── mcp/
│   │   ├── model/
│   │   │   ├── MCPRequest.java
│   │   │   ├── MCPResponse.java
│   │   │   ├── MCPError.java
│   │   │   ├── MCPTool.java
│   │   │   ├── MCPToolCallRequest.java
│   │   │   ├── MCPContent.java
│   │   │   └── MCPToolResponse.java
│   │   ├── service/
│   │   │   └── MCPService.java
│   │   ├── controller/
│   │   │   └── MCPController.java
│   │   └── MCPServerWrapper.java (standalone bridge)
│   └── config/
│       ├── DataInitializer.java
│       └── JacksonConfig.java
├── src/main/resources/
│   └── application.properties
├── mcp-server.sh (Linux/Mac)
├── mcp-server.bat (Windows)
└── pom.xml
```

## Architecture

This system uses a **Spring-native MCP server** where:
1. Spring Boot exposes an HTTP endpoint at `/mcp` that handles JSON-RPC 2.0 requests
2. A lightweight wrapper (shell script or Java) bridges stdio to the HTTP endpoint
3. Claude Desktop communicates with the wrapper, which forwards to Spring Boot

## Features

### REST APIs

1. **Get Patient by Name**: `GET /api/patients/by-name/{name}`
2. **Get Patient by ID**: `GET /api/patients/{id}`
3. **Get All Patients**: `GET /api/patients`
4. **Get Progress Notes**: `GET /api/patients/{id}/progress-notes`
5. **Get Care Plan**: `GET /api/patients/{id}/care-plan`
6. **Update Care Plan**: `POST /api/patients/{id}/care-plan`

### MCP Endpoint

**HTTP Endpoint**: `POST /mcp`

Accepts JSON-RPC 2.0 requests with methods:
- `initialize` - Initialize the MCP server
- `tools/list` - List available tools
- `tools/call` - Execute a tool

### MCP Tools (6 Available)

1. **get_patient_by_name** - Retrieve patient details by name
2. **get_patient_by_id** - Retrieve patient details by ID
3. **get_all_patients** - List all patients
4. **get_progress_notes** - Get progress notes for a patient
5. **get_care_plan** - Get care plan for a patient
6. **update_care_plan** - Update or create a care plan

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- curl (for shell script bridge)

### Step 1: Build and Run Spring Boot Application

```bash
# Navigate to project root
cd patient-care-system

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

You should see:
```
Patient Care System is running on http://localhost:8080
H2 Console available at http://localhost:8080/h2-console
```

### Step 2: Verify the Application

```bash
# Check health
curl http://localhost:8080/

# Test REST API
curl http://localhost:8080/api/patients

# Test MCP endpoint
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/list",
    "id": 1
  }'
```

### Step 3: Configure MCP Server Bridge

Choose ONE of the following options:

#### Option A: Shell Script Bridge (Linux/Mac) - RECOMMENDED

```bash
# Make the script executable
chmod +x mcp-server.sh

# Test it
echo '{"jsonrpc":"2.0","method":"tools/list","id":1}' | ./mcp-server.sh
```

#### Option B: Batch Script Bridge (Windows)

```cmd
# Test it
echo {"jsonrpc":"2.0","method":"tools/list","id":1} | mcp-server.bat
```

#### Option C: Java Wrapper Bridge (All Platforms)

```bash
# Compile
javac src/main/java/com/healthcare/patientcare/mcp/MCPServerWrapper.java

# Run
java -cp src/main/java com.healthcare.patientcare.mcp.MCPServerWrapper

# Test it
echo '{"jsonrpc":"2.0","method":"tools/list","id":1}' | java -cp src/main/java com.healthcare.patientcare.mcp.MCPServerWrapper
```

### Step 4: Configure Claude Desktop

Edit your Claude Desktop configuration file:

**Mac**: `~/Library/Application Support/Claude/claude_desktop_config.json`  
**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

Add the following configuration (choose based on your bridge choice):

#### For Shell Script (Linux/Mac):
```json
{
  "mcpServers": {
    "patient-care": {
      "command": "/absolute/path/to/patient-care-system/mcp-server.sh"
    }
  }
}
```

#### For Batch Script (Windows):
```json
{
  "mcpServers": {
    "patient-care": {
      "command": "C:\\absolute\\path\\to\\patient-care-system\\mcp-server.bat"
    }
  }
}
```

#### For Java Wrapper:
```json
{
  "mcpServers": {
    "patient-care": {
      "command": "java",
      "args": [
        "-cp",
        "/absolute/path/to/patient-care-system/src/main/java",
        "com.healthcare.patientcare.mcp.MCPServerWrapper"
      ]
    }
  }
}
```

**Important**: Replace the paths with your actual absolute paths!

### Step 5: Restart Claude Desktop

After updating the configuration, restart Claude Desktop. The MCP server will be available for use.

## Sample Data

The application automatically loads sample data on startup:

### Patient 1: John Smith
- **ID**: 1
- **Patient ID**: PAT001
- **Condition**: Hypertension
- **Progress Notes**: 2 notes
- **Care Plan**: Active

### Patient 2: Mary Johnson
- **ID**: 2
- **Patient ID**: PAT002
- **Condition**: Type 2 Diabetes
- **Progress Notes**: 1 note
- **Care Plan**: Active

## Using MCP Tools in Claude

Once configured, you can ask Claude to:

```
"Get patient details for John Smith"
"Show me the progress notes for patient ID 1"
"What is the care plan for Mary Johnson?"
"Update the care plan for patient 1 with new goals: Control blood pressure below 120/80"
```

## Testing MCP Server Manually

You can test the MCP endpoint directly:

```bash
# List tools
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/list",
    "id": 1
  }'

# Get patient by name
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/call",
    "params": {
      "name": "get_patient_by_name",
      "arguments": {
        "name": "John Smith"
      }
    },
    "id": 2
  }'

# Get progress notes
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/call",
    "params": {
      "name": "get_progress_notes",
      "arguments": {
        "patient_id": 1
      }
    },
    "id": 3
  }'

# Update care plan
curl -X POST http://localhost:8080/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "method": "tools/call",
    "params": {
      "name": "update_care_plan",
      "arguments": {
        "patient_id": 1,
        "goals": "Achieve blood pressure below 120/80",
        "interventions": "Daily exercise, low sodium diet",
        "medications": "Lisinopril 10mg daily",
        "status": "Active"
      }
    },
    "id": 4
  }'
```

## REST API Examples

### Get All Patients
```bash
curl http://localhost:8080/api/patients
```

### Get Patient by Name
```bash
curl http://localhost:8080/api/patients/by-name/John%20Smith
```

### Get Progress Notes
```bash
curl http://localhost:8080/api/patients/1/progress-notes
```

### Get Care Plan
```bash
curl http://localhost:8080/api/patients/1/care-plan
```

### Update Care Plan
```bash
curl -X POST http://localhost:8080/api/patients/1/care-plan \
  -H "Content-Type: application/json" \
  -d '{
    "goals": "New health goals",
    "interventions": "Updated interventions",
    "medications": "Updated medications",
    "startDate": "2025-01-01",
    "reviewDate": "2025-06-01",
    "status": "Active"
  }'
```

## H2 Database Console

Access the H2 console at: `http://localhost:8080/h2-console`

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:patientdb`
- Username: `sa`
- Password: (leave empty)

## Troubleshooting

### Spring Boot Issues

- **Port already in use**: Change the port in `application.properties`
- **Database connection**: Verify H2 console settings
- **Maven build fails**: Run `mvn clean install -U`

### MCP Server Issues

- **Connection refused**: Ensure Spring Boot is running on port 8080
- **MCP not showing in Claude**:
    - Check configuration path is absolute
    - Verify the bridge script is executable
    - Test the bridge manually with echo commands
- **Tools not appearing**:
    - Check Spring Boot logs for errors
    - Verify `/mcp` endpoint is accessible
    - Test with curl commands above

### Bridge Script Issues

- **Shell script not executing**: Run `chmod +x mcp-server.sh`
- **Java wrapper not found**: Ensure class is compiled and path is correct
- **stdin/stdout issues**: Check that the bridge properly forwards requests

## Architecture Benefits

Using Spring for the MCP server provides:

1. **Unified Codebase**: Single application serving both REST and MCP
2. **Dependency Injection**: Leverage Spring's IoC container
3. **Database Integration**: Direct access to JPA repositories
4. **Easy Testing**: Use Spring Boot test framework
5. **Production Ready**: Use Spring's robust production features
6. **No Node.js Dependency**: Pure Java solution

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data access layer
- **H2 Database** - In-memory database
- **Jackson** - JSON processing
- **Lombok** - Reduce boilerplate code
- **JSON-RPC 2.0** - MCP protocol implementation

## License

This is a sample project for educational purposes.