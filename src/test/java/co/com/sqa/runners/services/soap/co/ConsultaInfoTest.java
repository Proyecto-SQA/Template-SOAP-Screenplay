package co.com.sqa.runners.services.soap.co;

import org.junit.runner.RunWith;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        features = {"src/test/resources/soap/capitalCiudad.feature"},
        glue = {"co.com.sqa.runners.services.soap.co"}
)


public class ConsultaInfoTest {

}
