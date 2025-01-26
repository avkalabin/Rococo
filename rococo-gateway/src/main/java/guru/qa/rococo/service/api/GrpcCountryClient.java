package guru.qa.rococo.service.api;

import guru.qa.rococo.model.CountryJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class GrpcCountryClient {

    public Page<CountryJson> getAllCountry(Pageable pageable) {
        return null;
    }
}
