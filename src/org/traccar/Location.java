package org.traccar;

import org.traccar.model.Device;
import org.traccar.model.Position;
import java.util.Date;
public class Location  {
    public Location() {
    }

    public Location(Position position, Device device, String strAttributes) {
        this.name = device.getName();
        this.uniqueId = device.getUniqueId();
        this.status = device.getStatus();
        this.deviceId = position.getDeviceId();
        this.protocol = position.getProtocol();
        this.serverTime = position.getServerTime();
        this.deviceTime = position.getDeviceTime();
        this.fixTime = position.getFixTime();
        this.valid = position.getValid();
        this.latitude = position.getLatitude();
        this.longitude = position.getLongitude();
        this.altitude = position.getAltitude();
        this.speed = position.getSpeed();
        this.course = position.getCourse();
        this.accuracy = position.getAccuracy();
        this.statusCode = calculateStatus(position);
        this.attributes = strAttributes;
    }
    private String calculateStatus(Position position) {
        if (position.getAttributes().containsKey(Position.KEY_ALARM)) {
            return "0xF841"; // STATUS_PANIC_ON
        } else if (position.getSpeed() < 1.0) {
            return "0xF020"; // STATUS_LOCATION
        } else {
            return "0xF11C"; // STATUS_MOTION_MOVING
        }
    }
    private long deviceId;

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private String uniqueId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    private String protocol;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    private Date serverTime;

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

   private Date deviceTime;

    public Date getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(Date deviceTime) {
        this.deviceTime = deviceTime;
    }

    private Date fixTime;

    public Date getFixTime() {
        return fixTime;
    }

    public void setFixTime(Date fixTime) {
        this.fixTime = fixTime;
    }

    public void setTime(Date time) {
        setDeviceTime(time);
        setFixTime(time);
    }

    private boolean valid;

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double altitude; // value in meters

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    private double speed; // value in knots

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    private double course;

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String statusCode;

    public double getStatusCode() {
        return accuracy;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    private double accuracy;

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    private String attributes;

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    }
