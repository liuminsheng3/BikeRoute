package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "bikeroute-mobilehub-999666314-Routes")

public class RoutesDO {
    private String _userId;
    private String _date;
    private Double _used;
    private Double _endLatitude;
    private Double _endLongitude;
    private String _routeID;
    private String _routeName;
    private Double _startLatitude;
    private Double _startLongitude;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "Date")
    public String getDate() {
        return _date;
    }

    public void setDate(final String _date) {
        this._date = _date;
    }
    @DynamoDBAttribute(attributeName = "Used")
    public Double getUsed() {
        return _used;
    }

    public void setUsed(final Double _used) {
        this._used = _used;
    }
    @DynamoDBAttribute(attributeName = "endLatitude")
    public Double getEndLatitude() {
        return _endLatitude;
    }

    public void setEndLatitude(final Double _endLatitude) {
        this._endLatitude = _endLatitude;
    }
    @DynamoDBAttribute(attributeName = "endLongitude")
    public Double getEndLongitude() {
        return _endLongitude;
    }

    public void setEndLongitude(final Double _endLongitude) {
        this._endLongitude = _endLongitude;
    }
    @DynamoDBAttribute(attributeName = "routeID")
    public String getRouteID() {
        return _routeID;
    }

    public void setRouteID(final String _routeID) {
        this._routeID = _routeID;
    }
    @DynamoDBAttribute(attributeName = "routeName")
    public String getRouteName() {
        return _routeName;
    }

    public void setRouteName(final String _routeName) {
        this._routeName = _routeName;
    }
    @DynamoDBAttribute(attributeName = "startLatitude")
    public Double getStartLatitude() {
        return _startLatitude;
    }

    public void setStartLatitude(final Double _startLatitude) {
        this._startLatitude = _startLatitude;
    }
    @DynamoDBAttribute(attributeName = "startLongitude")
    public Double getStartLongitude() {
        return _startLongitude;
    }

    public void setStartLongitude(final Double _startLongitude) {
        this._startLongitude = _startLongitude;
    }

}
