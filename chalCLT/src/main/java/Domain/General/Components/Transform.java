package Domain.General.Components;

import Domain.CustumAnnotations.Imperial;
import Domain.CustumAnnotations.SerializeField;
import Domain.CustumAnnotations.SetPropertyEvent;
import Domain.General.Entity;
import Domain.Utility.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Transform extends Component {
    @SerializeField
    @SetPropertyEvent(eventTriggerMethodName = "setX")
    @Imperial
    private float x;
    @SerializeField
    @SetPropertyEvent(eventTriggerMethodName = "setY")
    @Imperial
    private float y;

    public Vector3 position;
    private Vector3 rotation;
    private final List<Transform> children;
    private Transform parent;

    protected Transform(Entity entity) {
        super(entity);
        position = new Vector3(0, 0, 0);
        rotation = new Vector3(0, 0, 0);
        children = new ArrayList<>();
        parent = null; // Initialize the parent to null (top-level object)
    }

    public Vector3 getWorldPosition() {
        if (parent != null) {
            // Recursively get the parent's world position
            Vector3 parentWorldPosition = parent.getWorldPosition();

            // Add the relative position to the parent's world position
            return parentWorldPosition.add(position);
        } else {
            // If there is no parent, the world position is the same as the local position
            return position;
        }
    }

    public Vector3 getRotation() {
        return new Vector3(rotation);
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
        normalizeRotation();
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
        normalizeRotation();
    }

    public void addRotation(Vector3 rotationDelta) {
        rotation.add(rotationDelta);
        normalizeRotation();
    }

    public void addRotation(float x, float y, float z) {
        rotation.add(new Vector3(x, y, z));
        normalizeRotation();
    }

    private void normalizeRotation() {
        rotation.x = rotation.x % 360;
        if (rotation.x < 0) {
            rotation.x = rotation.x + 360;
        }

        rotation.y = rotation.y % 360;
        if (rotation.y < 0) {
            rotation.y = rotation.y + 360;
        }

        rotation.z = rotation.z % 360;
        if (rotation.z < 0) {
            rotation.z = rotation.z + 360;
        }
    }

    public List<Transform> getChildren() {
        return children;
    }

    public Transform getParent() {
        return parent;
    }

    public void setParent(Transform newParent) {
        if (parent != null) {
            parent.children.remove(this);
        }

        parent = newParent;

        if (parent != null) {
            parent.children.add(this);
        }
    }

    public void setX(float x) {
        this.x = x;
        this.position.x = x;
    }

    public float getX() {
        return this.x;
    }

    public void setY(float y) {
        this.y = y;
        this.position.y = y;
    }

    public float getY() {
        return this.y;
    }
}

