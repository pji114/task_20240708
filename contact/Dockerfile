# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-oracle

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY build/libs/contact-0.0.1-SNAPSHOT.jar /app/contact-0.0.1-SNAPSHOT.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file with the required profile
ENTRYPOINT ["java", "-jar", "contact-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]
