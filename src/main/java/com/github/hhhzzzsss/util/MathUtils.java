package com.github.hhhzzzsss.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.*;
import org.lwjgl.system.CallbackI;

public class MathUtils {
    public static Vec3d getHitboxCenter(Entity entity) {
        Box bbox = entity.getBoundingBox();
        if (entity instanceof EnderDragonEntity) {
            bbox = ((EnderDragonEntity) entity).getBodyParts()[2].getBoundingBox();
        }
        return new Vec3d((bbox.minX + bbox.maxX)/2.0, (bbox.minY + bbox.maxY)/2.0, (bbox.minZ + bbox.maxZ)/2.0);
    }

    public static Double getLaunchAngle(double tx, double ty, double g, double d, double v) {
        if (tx < ty * 0.001) { // If it's near the asymptotes, just return a vertical angle
            return ty>0 ? Math.PI/2.0 : -Math.PI/2.0;
        }

        double md = 1.0-d;
        double log_md = Math.log(md);
        double g_d = g/d; // This is terminal velocity
        double theta = Math.atan2(ty, tx);
        double prev_abs_ydif = Double.POSITIVE_INFINITY;

        // 20 iterations max, although it usually converges in 3 iterations
        for (int i=0; i<20; i++) {
            double cost = Math.cos(theta);
            double sint = Math.sin(theta);
            double tant = sint/cost;
            double vx = v * cost;
            double vy = v * sint;
            double y = tx*(g_d+vy)/vx - g_d*Math.log(1-d*tx/vx)/log_md;
            double ydif = y-ty;
            double abs_ydif = Math.abs(ydif);

            // If it's getting farther away, there's probably no solution
            if (abs_ydif>prev_abs_ydif) {
                return null;
            }
            else if (abs_ydif < 0.0001) {
                return theta;
            }

            double dy_dtheta = tx + g*tx*tant / ((-d*tx+v*cost)*log_md) + g*tx*tant/(d*v*cost) + tx*tant*tant;
            theta -= ydif/dy_dtheta;
            prev_abs_ydif = abs_ydif;
        }

        // If exceeded max iterations, return null
        return null;
    }

    public static Vec3d offsetLookingAngle(Vec3d pos, Vec2f rotation, double u, double v, double w) {
        float f = MathHelper.cos((rotation.y + 90.0f) * ((float)Math.PI / 180));
        float g = MathHelper.sin((rotation.y + 90.0f) * ((float)Math.PI / 180));
        float h = MathHelper.cos(-rotation.x * ((float)Math.PI / 180));
        float i = MathHelper.sin(-rotation.x * ((float)Math.PI / 180));
        float j = MathHelper.cos((-rotation.x + 90.0f) * ((float)Math.PI / 180));
        float k = MathHelper.sin((-rotation.x + 90.0f) * ((float)Math.PI / 180));
        Vec3d wHat = new Vec3d(f * h, i, g * h);
        Vec3d vHat = new Vec3d(f * j, k, g * j);
        Vec3d uHat = wHat.crossProduct(vHat).multiply(-1.0);
        double dx = wHat.x * w + vHat.x * v + uHat.x * u;
        double dy = wHat.y * w + vHat.y * v + uHat.y * u;
        double dz = wHat.z * w + vHat.z * v + uHat.z * u;
        return new Vec3d(pos.x + dx, pos.y + dy, pos.z + dz);
    }

    public static Vec3d lookingAngleToVector(Vec2f rotation, double magnitude) {
        float pitch = rotation.x;
        float yaw = rotation.y;
        float x = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float y = -MathHelper.sin(pitch * ((float)Math.PI / 180));
        float z = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float len = MathHelper.sqrt(x * x + y * y + z * z);
        x *= magnitude / len;
        y *= magnitude / len;
        z *= magnitude / len;
        return new Vec3d(x, y, z);
    }
}
