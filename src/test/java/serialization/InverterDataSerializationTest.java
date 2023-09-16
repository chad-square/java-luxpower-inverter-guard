package serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.InverterData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class InverterDataSerializationTest {


    private static ObjectMapper OBJECT_MAPPER;

    @BeforeEach
    public void setup() {
        OBJECT_MAPPER = new ObjectMapper();
    }

    @Test
    public void serializeInverterData() throws IOException {


        InputStream fileInputStream = new FileInputStream("src/test/resources/gridPower.json");

        InverterData inverterData = OBJECT_MAPPER.readValue(fileInputStream, InverterData.class);
        System.out.println(inverterData);

    }

    @Test
    public void serializeInverterData2() throws IOException {


        InputStream fileInputStream = new FileInputStream("src/test/resources/backupPower.json");

        InverterData inverterData = OBJECT_MAPPER.readValue(fileInputStream, InverterData.class);
        System.out.println(inverterData);

    }


}
