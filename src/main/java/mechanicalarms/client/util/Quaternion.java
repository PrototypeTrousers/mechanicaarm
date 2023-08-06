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

    public static Quaternion createIdentity() {
        return new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    }

    public static Quaternion createRotate(float radian, float x, float y, float z) {
        radian *= 0.5F;
        float q = (float) Math.sin(radian);
        return new Quaternion(x * q, y * q, z * q, (float) Math.cos(radian));
    }

    public static Quaternion createRotateX(float radian) {
        radian *= 0.5F;
        return new Quaternion((float) Math.sin(radian), 0.0F, 0.0F, (float) Math.cos(radian));
    }

    public static Quaternion createRotateY(float radian) {
        radian *= 0.5F;
        return new Quaternion(0.0F, (float) Math.sin(radian), 0.0F, (float) Math.cos(radian));
    }

    public static Quaternion createRotateZ(float radian) {
        radian *= 0.5F;
        return new Quaternion(0.0F, 0.0F, (float) Math.sin(radian), (float) Math.cos(radian));
    }

    public void setIndentity() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.w = 1.0F;
    }

    public void multiply(Quaternion quaternion) {
        this.multiply(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public void multiply(float x, float y, float z, float w) {
        float x0 = this.x;
        float y0 = this.y;
        float z0 = this.z;
        float w0 = this.w;
        this.x =  x0 * w + y0 * z - z0 * y + w0 * x;
        this.y = -x0 * z + y0 * w + z0 * x + w0 * y;
        this.z =  x0 * y - y0 * x + z0 * w + w0 * z;
        this.w = -x0 * x - y0 * y - z0 * z + w0 * w;
    }

    public void rotate(float radian, float x, float y, float z) {
        radian *= 0.5F;
        float q = (float) Math.sin(radian);
        this.multiply(x * q, y * q, z * q, (float) Math.cos(radian));
    }

    public void rotateX(float radian) {
        radian *= 0.5F;
        float x = (float) Math.sin(radian);
        float w = (float) Math.cos(radian);
        float x0 = this.x;
        float y0 = this.y;
        float z0 = this.z;
        float w0 = this.w;
        this.x =  x0 * w + w0 * x;
        this.y =  y0 * w + z0 * x;
        this.z = -y0 * x + z0 * w;
        this.w = -x0 * x + w0 * w;
    }

    public void rotateY(float radian) {
        radian *= 0.5F;
        float y = (float) Math.sin(radian);
        float w = (float) Math.cos(radian);
        float x0 = this.x;
        float y0 = this.y;
        float z0 = this.z;
        float w0 = this.w;
        this.x =  x0 * w - z0 * y;
        this.y =  y0 * w + w0 * y;
        this.z =  x0 * y + z0 * w;
        this.w = -y0 * y + w0 * w;
    }

    public void rotateZ(float radian) {
        radian *= 0.5F;
        float z = (float) Math.sin(radian);
        float w = (float) Math.cos(radian);
        float x0 = this.x;
        float y0 = this.y;
        float z0 = this.z;
        float w0 = this.w;
        this.x =  x0 * w + y0 * z;
        this.y = -x0 * z + y0 * w;
        this.z =  z0 * w + w0 * z;
        this.w = -z0 * z + w0 * w;
    }

}
