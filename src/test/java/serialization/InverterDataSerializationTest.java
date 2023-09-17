package serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.InverterData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InverterDataSerializationTest {


    private static ObjectMapper OBJECT_MAPPER;

    @BeforeEach
    public void setup() {
        OBJECT_MAPPER = new ObjectMapper();
    }

    @Test
    public void convertJsonToInverterData() throws IOException {
        InputStream fileInputStream = new FileInputStream("src/test/resources/gridPower.json");

        InverterData inverterData = OBJECT_MAPPER.readValue(fileInputStream, InverterData.class);

        assertEquals(90, inverterData.batteryCharge());
        assertEquals(420, inverterData.grid());
        assertEquals(0, inverterData.solar());
        assertEquals(0, inverterData.backupPower());
    }

    @Test
    public void convertJsonToInverterData2() throws IOException {
        InputStream fileInputStream = new FileInputStream("src/test/resources/backupPower.json");

        InverterData inverterData = OBJECT_MAPPER.readValue(fileInputStream, InverterData.class);

        assertEquals(82, inverterData.batteryCharge());
        assertEquals(0, inverterData.grid());
        assertEquals(0, inverterData.solar());
        assertEquals(416, inverterData.backupPower());
    }


    @Test
    public void convertToJson() throws IOException {
        InverterData data = new InverterData(90, 420, 0, 0);

        String s = OBJECT_MAPPER.writeValueAsString(data);

        assertEquals("{\"soc\":90,\"pToUser\":420,\"ppv\":0,\"peps\":0}", s);
    }

}
