package Domain.Rendering;

import Domain.Utility.Matrix4;
import Domain.Utility.Vector3;

public class Camera {
    private Vector3 position;
    private double pitch;  // Rotation around the X-axis
    private double yaw;    // Rotation around the Y-axis
    private double roll;   // Rotation around the Z-axis
    private double fov;    // Field of view
    private double aspectRatio;
    private double near;   // Near clipping plane
    private double far;    // Far clipping plane
    private Matrix4 viewMatrix;  // The view matrix

    private Vector3 up = new Vector3(0, 1, 0);
    private Vector3 right = new Vector3(1, 0, 0);

    private Vector3 forward;
    private Vector3 left;




    double MIN_ZOOM = 1.0;  // Adjust this according to your scene
    double MAX_ZOOM = 10.0; // Adjust this according to your scene

    public Camera(Vector3 position) {
        this.position = position;
        this.pitch = 0.0;
        this.yaw = 0.0;
        this.roll = 0.0;
        this.fov = 60.0;  // Default field of view in degrees
        this.aspectRatio = 1.0;  // Default aspect ratio (1:1)
        this.near = 0.1;  // Default near clipping plane
        this.far = 100.0;  // Default far clipping plane
        this.viewMatrix = new Matrix4();
        this.viewMatrix.setIdentity(); // Set the view matrix to the identity matrix initially
        forward = calculateViewDirection();
        left = up.crossProduct(forward).normalize();
    }

    public void rotate(double deltaX, double deltaY) {
        // Update pitch and yaw angles based on mouse input
        pitch += deltaY;
        yaw += deltaX;

        // Limit pitch to avoid flipping
        pitch = Math.max(Math.min(pitch, 90.0), -90.0);

        // Update the view matrix to apply the new rotation
        updateViewMatrix();
    }


    // This method updates the view matrix with the current position, pitch, and yaw
    private void updateViewMatrix() {

        viewMatrix = new Matrix4();
        viewMatrix.setIdentity();

        // Apply translation
        viewMatrix.translate(-position.x, -position.y, -position.z);

        // Calculate the new view direction based on pitch and yaw angles
        forward = calculateViewDirection();

        // Calculate the new left vector based on the view direction and the up vector
        left = up.crossProduct(forward).normalize();

        // Calculate the new up vector based on the view direction and the left vector
        up = forward.crossProduct(left).normalize();

        right = forward.crossProduct(up).normalize();

        // Apply rotation
        viewMatrix.rotate(-pitch, 1, 0, 0);
        viewMatrix.rotate(-yaw, 0, 1, 0);
    }


    public void setProjection(double fov, double aspectRatio, double near, double far) {
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
    }

    public Matrix4 getViewMatrix() {
        Matrix4 viewMatrix = new Matrix4();
        viewMatrix.setIdentity();

        // Apply translation
        viewMatrix.translate(-position.x, -position.y, -position.z);

        // Apply rotation
        viewMatrix.rotate(-pitch, 1, 0, 0);
        viewMatrix.rotate(-yaw, 0, 1, 0);

        return viewMatrix;
    }

    public Matrix4 getProjectionMatrix() {
        double f = 1.0 / Math.tan(Math.toRadians(fov / 2.0));
        double zRange = near - far;

        Matrix4 projectionMatrix = new Matrix4();
        projectionMatrix.set(0, f / aspectRatio);
        projectionMatrix.set(5, f);
        projectionMatrix.set(10, (far + near) / zRange);
        projectionMatrix.set(11, (2 * far * near) / zRange);
        projectionMatrix.set(14, -1);

        return projectionMatrix;
    }

    public void zoom(double zoomAmount) {

        System.out.println("zoom");
        // Calculate the zoom direction based on the camera's view direction
        Vector3 zoomDirection = calculateViewDirection().normalize(); // You need to implement getViewDirection()

        // Update the camera's position based on the zoom direction and zoom amount
        position.add(zoomDirection.multiply(zoomAmount));

        // Ensure any necessary zoom limits here, for example:
        position.z = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, position.z));

        // Update the view matrix to apply the new position
        updateViewMatrix();
    }


    public Vector3 calculateViewDirection() {
        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);

        double x = Math.cos(pitchRadians) * Math.cos(yawRadians);
        double y = Math.sin(pitchRadians);
        double z = Math.cos(pitchRadians) * Math.sin(yawRadians);

        return new Vector3(x, y, z);
    }


    public void move(double horizontal, double vertical) {
        // Calculate the movement vector based on the horizontal and vertical parameters
        Vector3 horizontalMovement = left.multiply(horizontal);
        Vector3 verticalMovement = up.multiply(vertical);

        // Combine the horizontal and vertical movements
        Vector3 movement = horizontalMovement.add(verticalMovement);
        System.out.println(movement);

        // Update the camera's position based on the movement vector
        position.add(movement);

        // Update the view matrix to apply the new position
        updateViewMatrix();
    }

    public Vector3 getForwardVector() {
        return forward.normalize();
    }

    public Vector3 getLeftVector() {
        return right.multiply(-1).normalize();
    }

    public Vector3 getUpVector() {
        return up.normalize();
    }



    public Matrix4 getProjectionViewMatrix() {
        return Matrix4.multiply(getProjectionMatrix(), getViewMatrix());
    }

}