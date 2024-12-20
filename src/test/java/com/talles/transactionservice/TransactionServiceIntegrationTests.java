package com.talles.transactionservice;

import com.talles.transactionservice.exception.BusinessErrorResponse;
import com.talles.transactionservice.exception.StandardErrorResponse;
import com.talles.transactionservice.model.dto.ConversionDetails;
import com.talles.transactionservice.model.dto.TransactionCreationDto;
import com.talles.transactionservice.model.dto.TransactionDto;
import com.talles.transactionservice.model.entity.Transaction;
import com.talles.transactionservice.repository.TransactionRepository;
import com.talles.transactionservice.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TransactionServiceApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@EnabledIf(expression = "#{environment.acceptsProfiles('integration-tests')}", loadContext = true)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@Slf4j
class TransactionServiceIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransactionRepository transactionRepository;

	@AfterEach
	public void resetDb() {
		transactionRepository.deleteAll();
	}

	@Test
	void createTransactionWithoutAmount() throws Exception {
		TransactionCreationDto transactionCreationDto = new TransactionCreationDto();
		LocalDateTime localDateTime = LocalDateTime.now();
		transactionCreationDto.setTransactionDate(localDateTime.truncatedTo(ChronoUnit.SECONDS));
		transactionCreationDto.setDescription("Transaction Invalid 1");

		MvcResult result = mockMvc.perform(post("/v1/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtils.toJson(transactionCreationDto)))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andReturn();

		// Check API response body.
		String responseBody = result.getResponse().getContentAsString();
		StandardErrorResponse standardErrorResponse = JsonUtils.fromJson(responseBody, StandardErrorResponse.class);
		assertThat(standardErrorResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(standardErrorResponse.getMessage()).isEqualTo("Invalid Request");
		assertThat(standardErrorResponse.getDetails()).containsKey("amountUSD");

	}

	@Test
	void createAndGetValidTransactionWithDate() throws Exception {
		TransactionCreationDto transactionCreationDto = new TransactionCreationDto();
		LocalDateTime localDateTime = LocalDateTime.now();
		transactionCreationDto.setTransactionDate(localDateTime.truncatedTo(ChronoUnit.SECONDS));
		transactionCreationDto.setDescription("Transaction Test 1");
		transactionCreationDto.setAmountUSD(new BigDecimal("38.10"));

		MvcResult result = mockMvc.perform(post("/v1/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonUtils.toJson(transactionCreationDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andReturn();

		// Check API response body.
		String responseBody = result.getResponse().getContentAsString();
		TransactionDto transactionResponse = JsonUtils.fromJson(responseBody, TransactionDto.class);

		assertThat(transactionResponse.getTransactionDate()).isEqualTo(transactionCreationDto.getTransactionDate());
		assertThat(transactionResponse.getDescription()).isEqualTo(transactionCreationDto.getDescription());
		assertThat(transactionResponse.getAmountUSD()).isEqualTo(transactionCreationDto.getAmountUSD());

		// Check repository
		List<Transaction> storedTransactions = transactionRepository.findAll();
		assertThat(storedTransactions).extracting(Transaction::getTransactionDate).containsOnly(transactionCreationDto.getTransactionDate());
		assertThat(storedTransactions).extracting(Transaction::getDescription).containsOnly(transactionCreationDto.getDescription());
		assertThat(storedTransactions).extracting(Transaction::getAmountUSD).containsOnly(transactionCreationDto.getAmountUSD());

		// Get one transaction
		result = mockMvc.perform(get("/v1/transactions/{id}", transactionResponse.getId())
						.queryParam("countryCurrency", "Argentina-Peso"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();

		responseBody = result.getResponse().getContentAsString();
		transactionResponse = JsonUtils.fromJson(responseBody, TransactionDto.class);

		assertThat(transactionResponse.getTransactionDate()).isEqualTo(transactionCreationDto.getTransactionDate());
		assertThat(transactionResponse.getDescription()).isEqualTo(transactionCreationDto.getDescription());
		assertThat(transactionResponse.getAmountUSD()).isEqualTo(transactionCreationDto.getAmountUSD());

		ConversionDetails conversionDetails = transactionResponse.getConversionDetails();
		assertThat(conversionDetails).isNotNull();
		assertThat(conversionDetails.getCountryCurrency()).isEqualTo("Argentina-Peso");
		assertThat(conversionDetails.getAmount()).isPositive();
	}

	@Test
	void getTransactionWithInvalidCurrency() throws Exception {
		TransactionCreationDto transactionCreationDto = new TransactionCreationDto();
		LocalDateTime localDateTime = LocalDateTime.now();
		transactionCreationDto.setTransactionDate(localDateTime.truncatedTo(ChronoUnit.SECONDS));
		transactionCreationDto.setDescription("Transaction Test 1");
		transactionCreationDto.setAmountUSD(new BigDecimal("38.10"));

		MvcResult result = mockMvc.perform(post("/v1/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtils.toJson(transactionCreationDto)))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andReturn();

		// Check API response body.
		String responseBody = result.getResponse().getContentAsString();
		TransactionDto transactionResponse = JsonUtils.fromJson(responseBody, TransactionDto.class);

		assertThat(transactionResponse.getTransactionDate()).isEqualTo(transactionCreationDto.getTransactionDate());
		assertThat(transactionResponse.getDescription()).isEqualTo(transactionCreationDto.getDescription());
		assertThat(transactionResponse.getAmountUSD()).isEqualTo(transactionCreationDto.getAmountUSD());

		// Check repository
		List<Transaction> storedTransactions = transactionRepository.findAll();
		assertThat(storedTransactions).extracting(Transaction::getTransactionDate).containsOnly(transactionCreationDto.getTransactionDate());
		assertThat(storedTransactions).extracting(Transaction::getDescription).containsOnly(transactionCreationDto.getDescription());
		assertThat(storedTransactions).extracting(Transaction::getAmountUSD).containsOnly(transactionCreationDto.getAmountUSD());

		// Get one transaction
		result = mockMvc.perform(get("/v1/transactions/{id}", transactionResponse.getId())
						.queryParam("countryCurrency", "Mars-Peso"))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andReturn();

		responseBody = result.getResponse().getContentAsString();
		BusinessErrorResponse businessErrorResponse = JsonUtils.fromJson(responseBody, BusinessErrorResponse.class);
		assertThat(businessErrorResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(businessErrorResponse.getMessage()).isNotBlank();
	}

	@Test
	void getTransaction_NotFound() throws Exception {
		// Get one transaction
		MvcResult result = mockMvc.perform(get("/v1/transactions/{id}", 1)
						.queryParam("countryCurrency", "Argentina-Peso"))
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andReturn();

		assertThat(result.getResponse().getContentLength()).isZero();
	}

}
