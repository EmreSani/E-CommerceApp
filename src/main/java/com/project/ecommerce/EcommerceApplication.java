package com.project.ecommerce;

import com.project.ecommerce.entity.concretes.user.UserRole;
import com.project.ecommerce.entity.enums.Gender;
import com.project.ecommerce.entity.enums.RoleType;
import com.project.ecommerce.payload.request.business.ProductRequest;
import com.project.ecommerce.payload.request.user.UserRequest;
import com.project.ecommerce.repository.user.UserRoleRepository;
import com.project.ecommerce.service.business.ProductService;
import com.project.ecommerce.service.user.UserRoleService;
import com.project.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
@RequiredArgsConstructor
public class EcommerceApplication implements CommandLineRunner {

	private final UserRoleService userRoleService;
	private final UserRoleRepository userRoleRepository;
	private final UserService userService;
	private final ProductService productService;

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if(userRoleService.getAllUserRole().isEmpty()){

			UserRole admin = new UserRole();
			admin.setRoleType(RoleType.ADMIN);
			admin.setRoleName("Admin");
			userRoleRepository.save(admin);

			UserRole customer = new UserRole();
			customer.setRoleType(RoleType.CUSTOMER);
			customer.setRoleName("Customer");
			userRoleRepository.save(customer);

		}
		// Built_in Admin olusturuluyor eger sistemde Admin yoksa
		if(userService.countAllAdmins()==0){

			UserRequest adminRequest = new UserRequest();
			adminRequest.setUsername("Admin");
			adminRequest.setEmail("admin@admin.com");
			adminRequest.setPassword("123456");
			adminRequest.setName("Emre");
			adminRequest.setSurname("Sunny");
			adminRequest.setPhoneNumber("111-111-1111");
			adminRequest.setGender(Gender.MALE);
			adminRequest.setBirthDay(LocalDate.of(1980,2,2));
			adminRequest.setBirthPlace("Ankara");
			userService.saveUser(adminRequest,"Admin");
		}

		if (productService.findAllProductsForMainClass().isEmpty()){

			ProductRequest productRequest = new ProductRequest();
			productRequest.setBrand("ExampleProduct");
			productRequest.setProductName("ExampleProduct");
			productRequest.setPrice(100.0);
			productRequest.setQuantity(1000.0);

			productService.addProduct(productRequest);

			ProductRequest productRequest2 = new ProductRequest();
			productRequest2.setBrand("ExampleProduct2");
			productRequest2.setProductName("ExampleProduct2");
			productRequest2.setPrice(100.0);
			productRequest2.setQuantity(1000.0);

			productService.addProduct(productRequest2);

		}

	}
}
