package org.adaschool.tdd;

import org.adaschool.tdd.controller.weather.dto.NearByWeatherReportsQueryDto;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.adaschool.tdd.service.WeatherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class WeatherReportControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    WeatherService weatherService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createWeatherReportCallsSaveOnService() throws Exception{

        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReportDto weatherReportDto = new WeatherReportDto( location, 35f, 22f, "tester", new Date() );

        WeatherReport weatherReport = new WeatherReport(weatherReportDto);

        when( weatherService.report( weatherReportDto )).thenReturn(weatherReport);
        WeatherReport weatherReportCreated = this.restTemplate.postForEntity("http://localhost:" + port + "/v1/weather" , weatherReportDto , WeatherReport.class ).getBody();
        Assertions.assertTrue(weatherReport.equals(weatherReportCreated));
    }

    @Test
    public void weatherReportIdFoundTest(){
        String weatherReportId = "awae-asd45-1dsad";
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        when( weatherService.findById( weatherReportId ) ).thenReturn( weatherReport );

        WeatherReport weatherReportFound  = this.restTemplate.getForObject("http://localhost:" + port + "/v1/weather/"+weatherReportId,WeatherReport.class);
        Assertions.assertTrue(weatherReport.equals(weatherReportFound));

    }
    @Test
    public void nearestWeatherReportFoundTest(){

        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        List<WeatherReport> weatherReportList = new ArrayList<>();
        weatherReportList.add(weatherReport);

        NearByWeatherReportsQueryDto nearByWeatherReportsQueryDto = new NearByWeatherReportsQueryDto(location,10);
        when( weatherService.findNearLocation(location,10)).thenReturn(weatherReportList);


        List<WeatherReport> weatherReportsNearest = Arrays.asList(this.restTemplate.postForEntity("http://localhost:" + port + "/v1/weather/" + "nearby", nearByWeatherReportsQueryDto, WeatherReport[].class).getBody());
        Assertions.assertEquals(weatherReportList,weatherReportsNearest);

    }

    @Test
    void weatherReportByReporterFoundTest() {
        double lat = 4.7110;
        double lng = 74.0721;
        GeoLocation location = new GeoLocation( lat, lng );
        WeatherReport weatherReport = new WeatherReport( location, 35f, 22f, "tester", new Date() );
        List<WeatherReport> weatherReportList = new ArrayList<>();
        weatherReportList.add(weatherReport);

        when( weatherService.findWeatherReportsByName("tester")).thenReturn(weatherReportList);
        List<WeatherReport> weatherReportsByName = Arrays.asList(this.restTemplate.getForObject("http://localhost:" + port + "/v1/weather/" + "reporter/"+"tester", WeatherReport[].class));

        Assertions.assertEquals(weatherReportList,weatherReportsByName);

    }

}
