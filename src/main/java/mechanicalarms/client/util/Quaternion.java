package mechanicalarms.client.util;

public class Quaternion {

    public float x;
    public float y;
    public float z;
    public float w;

    public Quaternion() {

    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Quaternion quaternion = (Quaternion) obj;
            return Float.compare(quaternion.x, this.x) == 0
                    && Float.compare(quaternion.y, this.y) == 0
                    && Float.compare(quaternion.z, this.z) == 0
                    && Float.compare(quaternion.w, this.w) == 0;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.x != 0.0F ? Float.floatToIntBits(this.x) : 0;
        i = 31 * i + (this.y != 0.0F ? Float.floatToIntBits(this.y) : 0);
        i = 31 * i + (this.z != 0.0F ? Float.floatToIntBits(this.z) : 0);
        return 31 * i + (this.w != 0.0F ? Float.floatToIntBits(this.w) : 0);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("Quaternion: ");
        stringbuilder.append(this.x);
        stringbuilder.append(" ");
        stringbuilder.append(this.y);
        stringbuilder.append(" ");
        stringbuilder.append(this.z);
        stringbuilder.append(" ");
        stringbuilder.append(this.w);
        return stringbuilder.toString();
    }

    public static Quaternion ToQuaternion(double pitch, double yaw) // pitch (x), yaw (Y)
    {
        // Abbreviations for the various angular functions

        double cr = Math.cos(pitch * 0.5);
        double sr = Math.sin(pitch * 0.5);
        double cp = Math.cos(yaw * 0.5);
        double sp = Math.sin(yaw * 0.5);

        Quaternion q = new Quaternion();
        q.w = (float) (cr * cp);
        q.x = (float) (sr * cp);
        q.y = (float) (cr * sp );
        q.z = (float) (- sr * sp);

        return q;
    }
}
