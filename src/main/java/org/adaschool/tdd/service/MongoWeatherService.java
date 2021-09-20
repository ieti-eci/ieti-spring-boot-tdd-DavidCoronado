package org.adaschool.tdd.service;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MongoWeatherService
    implements WeatherService
{

    private final WeatherReportRepository repository;

    public MongoWeatherService( @Autowired WeatherReportRepository repository )
    {
        this.repository = repository;
    }

    @Override
    public WeatherReport report( WeatherReportDto weatherReportDto )
    {
        WeatherReport weatherReport = new WeatherReport(weatherReportDto);
        return repository.save(weatherReport);
    }

    @Override
    public WeatherReport findById( String id ) throws WeatherReportNotFoundException
    {
        Optional<WeatherReport> optionalWeather = repository.findById(id);
        if(!optionalWeather.isPresent()){
            throw new WeatherReportNotFoundException();
        }
        return optionalWeather.get();
    }

    @Override
    public List<WeatherReport> findNearLocation( GeoLocation geoLocation, float distanceRangeInMeters )
    {
        List<WeatherReport> weatherReports = repository.findAll();
        List<WeatherReport> nearestWeatherReports = new ArrayList<>();
        for (WeatherReport wr: weatherReports){
            if(calculateDistance(geoLocation, wr.getGeoLocation())<=distanceRangeInMeters){
                nearestWeatherReports.add(wr);
            }
        }
        return nearestWeatherReports;
    }

    @Override
    public List<WeatherReport> findWeatherReportsByName( String reporter )
    {
        return repository.findByReporter(reporter);
    }

    private float calculateDistance(GeoLocation g1, GeoLocation g2){

        float distanceY = ((float) g1.getLat()) -((float) g2.getLat());
        float distanceX = ((float) g1.getLng()) - ((float) g2.getLng());
        float totalDistance = (float) Math.sqrt(Math.pow(distanceX,2) + Math.pow(distanceY,2));
        return totalDistance;
    }

}
