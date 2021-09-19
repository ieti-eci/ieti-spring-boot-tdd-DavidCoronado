package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.MongoWeatherService;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
class MongoWeatherServiceTest
{
    WeatherService weatherService;

    @Mock
    WeatherReportRepository repository;

    @BeforeAll()
    public void setup()
    {
        weatherService = new MongoWeatherService( repository );
    }

    @Test
    void createWeatherReportCallsSaveOnRepository()
    {
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );
        weatherService.report( weatherReportDto );
        verify( repository ).save( any( WeatherReport.class ) );
    }

    @Test
    void weatherReportIdFoundTest()
    {
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.of( weatherReport ) );
        WeatherReport foundWeatherReport = weatherService.findById( weatherReportId );
        Assertions.assertEquals( weatherReport, foundWeatherReport );
    }

    @Test
    void weatherReportIdNotFoundTest()
    {
        String weatherReportId = "dsawe1fasdasdoooq123";
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.empty() );
        Assertions.assertThrows( WeatherReportNotFoundException.class, () -> {
            weatherService.findById( weatherReportId );
        } );
    }

    @Test
    void nearestWeatherReportFoundTest(){
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 10.0;
        double lng = 10.0;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );

        String weatherReportId2 = "awae-asd45-1qwer";
        double lat2 = 15.0;
        double lng2 = 15.0;
        GeoLocation location2 = new GeoLocation( lat2, lng2 );
        WeatherReport weatherReport2 = new WeatherReport( location2, 35f, 22f, "tester", new Date() );

        List<WeatherReport> weatherReports = new ArrayList<>();
        weatherReports.add(weatherReport);
        weatherReports.add(weatherReport2);

        when(repository.findAll()).thenReturn(weatherReports);

        double lat3 = 5.0;
        double lng3 = 5.0;
        GeoLocation location3 = new GeoLocation( lat3, lng3 );

        Assertions.assertEquals( 1, weatherService.findNearLocation(location3,8).size() );
    }

    @Test
    void weatherReportByReporterFoundTest() {
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 10.0;
        double lng = 10.0;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        List<WeatherReport> weatherReports = new ArrayList<>();
        weatherReports.add(weatherReport);
        when( repository.findByReporter( "tester" ) ).thenReturn( weatherReports );

        Assertions.assertEquals(1,weatherService.findWeatherReportsByName("tester").size());


    }

}
