# Advanced_SWEeties-


## Charity Kitchen API

Welcome to the **Charity Kitchen API**. This API provides information about nearby charity kitchens and allows users to interact with the system by retrieving kitchen details, predicting waiting times, submitting ratings, and managing users.

## How to set up the project
The whole repository is developed and tested on Mac book, so the following instructions are for Mac users. If you are using Windows or Linux, you may need to adjust the commands! Before running the project, please make sure the following is installed on your device.
- JDK17: All functions is tested on JDK17 and it is highly recommended that you use the same version! You can check your current version by running `java -version` in the terminal. For installation, you can visit [here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).
- Maven 3.9.5: You can check your current version by running `mvn -version` in the terminal. For installation, you can visit [here](https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.5/).

### Steps to run the project
After cloning the repository, you can run the project by following the steps below:
- Compile the project: You should first compile it with
```
mvn clean compile
```
This will compile the project and download all the dependencies required for the project.
- Run the project: You can run the project by running the following command:
```
mvn spring-boot:run
```
- Go to your browser and type `http://localhost:8080/api/` to see the welcome message of the API.
- You can type `http://localhost:8080/api/kitchens/nearest?address=columbia%20university&count=4` to see the nearest kitchens to Columbia University. (This is our sample query, you can change the address and count as you wish!)

### Table of Contents for API
- [Home](#home)
- [Get Nearest Kitchens](#get-nearest-kitchens)
- [Get Top Rated Kitchens](#get-top-rated-kitchens)
- [Get Kitchen Details](#get-kitchen-details)
- [Predict Waiting Times](#predict-waiting-times)
- [Submit Rating](#submit-rating)
- [User Login](#user-login)
- [Update Kitchen Info](#update-kitchen-info)
- [Add Kitchen](#add-kitchen)
- [Delete User](#delete-user)

### Note for Developers
- we are using lombok for getter and setter methods, lombok dependency is added in the pom.xml file, but developers need to make sure the annotation processing for lombok is enabled in the IDE: go to File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors and check the box for Enable annotation processing.
---

## Home
### `GET /api/`
**Description**: Welcome message for the API.

**Response**:
- `200 OK`: Returns a welcome message.

---

## Get Nearest Kitchens
### `GET /api/kitchens/nearest`
**Description**: Retrieves a specified number of nearest charity kitchens based on the user’s geographical location.

**Query Parameters**:
- `address` (String) - The user's current location.
- `count` (int) - The number of charity kitchens to return.

**Response**:
- `200 OK`: Returns a list of kitchens with details such as name, address, distance, rating, and accessibility features.
- `400 Bad Request`: If the parameters are invalid.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Get Top Rated Kitchens
### `GET /api/kitchens/top-rated`
**Description**: Provides a list of top-rated kitchens sorted by a combination of ratings and distance, adjusted for service quality.

**Query Parameters**:
- `count` (int) - The number of top-rated charity kitchens to return.

**Response**:
- `200 OK`: Successfully returns the list of top-rated charity kitchens.
- `400 Bad Request`: If the parameters are invalid.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Get Kitchen Details
### `GET /api/kitchens/details`
**Description**: Fetches detailed information about a specific charity kitchen, including operational status, operating hours, and services offered.

**Query Parameters**:
- `kitchen_id` (Long) - Unique identifier for the charity kitchen.

**Response**:
- `200 OK`: Returns detailed kitchen information.
- `404 Not Found`: If the `kitchen_id` does not correspond to an existing kitchen.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Predict Waiting Times
### `GET /api/kitchens/wait-times`
**Description**: Uses machine learning models to predict average waiting times based on historical data.

**Query Parameters**:
- `kitchen_id` (Long) - Unique identifier for the charity kitchen.

**Response**:
- `200 OK`: Returns the predicted average waiting time.
- `404 Not Found`: If `kitchen_id` does not exist.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Submit Rating
### `POST /api/kitchens/rate`
**Description**: Allows users to rate a charity kitchen. Ratings are used to update the kitchen’s overall rating.

**Request Body**:
```json
{
  "kitchen_id": Long,
  "user_id": Long,
  "rating": int
}
```

**Response**:
- `200 OK`: Rating successfully recorded.
- `400 Bad Request`: If the input parameters are invalid.
- `404 Not Found`: If `kitchen_id` or `user_id` does not exist.
- `500 Internal Server Error`: For unexpected backend errors.

---

## User Login
### `POST /api/users/login`
**Description**: Authenticates user credentials and returns an API key for session management.

**Request Body**:
```json
{
  "username": "String",
  "password": "String"
}
```

**Response**:
- `200 OK`: Returns API key and user details.
- `401 Unauthorized`: If credentials are incorrect.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Update Kitchen Info
### `PUT /api/kitchens/update`
**Required Role**: `SUPER_GOLDEN_PLUS` or `MANAGER`

**Description**: Allows authorized users to update the details of a charity kitchen.

**Request Body**:
```json
{
  "kitchen_id": Long,
  "new_details": {
    "address": "String",
    "operating_hours": "String",
    "accessibility_features": "String",
    "services_offered": "String"
  }
}
```

**Response**:
- `200 OK`: Kitchen information updated successfully.
- `403 Forbidden`: If the user does not have sufficient privileges.
- `404 Not Found`: If `kitchen_id` does not exist.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Add Kitchen
### `POST /api/kitchens/add`
**Required Role**: `SUPER_GOLDEN_PLUS` or `MANAGER`

**Description**: Allows authorized users to add a new charity kitchen to the system.

**Request Body**:
```json
{
  "new_kitchen_details": {
    "name": "String",
    "address": "String",
    "operating_hours": "String",
    "accessibility_features": "String",
    "services_offered": "String"
  }
}
```

**Response**:
- `201 Created`: New kitchen added successfully.
- `403 Forbidden`: If the user does not have sufficient privileges.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Delete User
### `DELETE /api/users/delete`
**Required Role**: `MANAGER`

**Description**: Allows authorized users to delete a user from the system.

**Query Parameters**:
- `user_id` (Long) - Unique identifier for the user to be deleted.

**Response**:
- `200 OK`: User deleted successfully.
- `403 Forbidden`: If the user does not have sufficient privileges.
- `500 Internal Server Error`: For unexpected backend errors.

---

## Running the Application

To run the application locally, use the following commands:

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Once the application is running, you can access the API at `http://localhost:8080/api/`.

---

## GCloud Instance 
- Currently, the project requires manual deployment to GCP Instance. We configured our GCP App Engine according to the class instructions. As of October 18, we have an instance running. The instance is reachable at https://advancedsweeties.uk.r.appspot.com/api/.

## Geocoding API
- We are using the Google Geocoding API to convert addresses to latitude and longitude coordinates. The API key is stored in the `application.properties` file. We implemented a service class to handle the API requests and responses. For the full implementation, please refer to the service/impl/KitchenServiceImpl (method: fetchAllKitchens). For the full documentation of the Geocoding API, please visit [here](https://developers.google.com/maps/documentation/geocoding/overview).  

## License

This project is licensed under the MIT License.
