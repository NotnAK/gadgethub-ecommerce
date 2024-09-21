package com.example.ecommerce;

import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.entity.Brand;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.repository.BrandRepository;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CustomerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;

    public DataLoader(CategoryRepository categoryRepository, BrandRepository brandRepository,
                      ProductRepository productRepository, PasswordEncoder passwordEncoder,
                      CustomerService customerService) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerService = customerService;
    }

    @Override
    public void run(String... args) throws Exception {
        CustomerDTO admin = new CustomerDTO();


        //admin
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);
        admin.setPassword(passwordEncoder.encode("123"));
        customerService.createCustomer(admin);


        CustomerDTO user = new CustomerDTO();
        user.setEmail("user@example.com");
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode("123"));
        customerService.createCustomer(user);


        Category electronics = new Category();
        electronics.setName("Smartphones");
        electronics.setDescription("Smartphones");
        categoryRepository.save(electronics);

        Category laptops = new Category();
        laptops.setName("Laptops");
        laptops.setDescription("Different laptops");
        categoryRepository.save(laptops);

        Category accessories = new Category();
        accessories.setName("Accessories");
        accessories.setDescription("Various electronics accessories");
        categoryRepository.save(accessories);


        Brand apple = new Brand();
        apple.setName("Apple");
        apple.setDescription("Leading electronics brand");
        brandRepository.save(apple);


        Brand samsung = new Brand();
        samsung.setName("Samsung");
        samsung.setDescription("Popular electronics manufacturer");
        brandRepository.save(samsung);

        Brand xiaomi = new Brand();
        xiaomi.setName("Xiaomi");
        xiaomi.setDescription("Known for laptops and accessories");
        brandRepository.save(xiaomi);

        Brand hp = new Brand();
        hp.setName("HP");
        hp.setDescription("HP laptops and peripherals");
        brandRepository.save(hp);

        Brand dell = new Brand();
        dell.setName("Dell");
        dell.setDescription("High-quality laptops and desktops");
        brandRepository.save(dell);

        Brand google = new Brand();
        google.setName("Google");
        google.setDescription("Tech giant producing smartphones and smart devices");
        brandRepository.save(google);

        Brand asus = new Brand();
        asus.setName("Asus");
        asus.setDescription("Known for laptops, gaming devices, and electronics");
        brandRepository.save(asus);

        Product iphone = new Product();
        iphone.setName("Apple iPhone 15 128 GB");
        iphone.setDescription("Latest model of iPhone with new features.");
        iphone.setPrice(BigDecimal.valueOf(799));
        iphone.setQuantity(2);
        iphone.setCategory(electronics);
        iphone.setBrand(apple);
        iphone.setImageUrl("/images/iphone_15_black.jpg");
        productRepository.save(iphone);

        Product galaxy = new Product();
        galaxy.setName("Samsung Galaxy S24 8 GB/256 GB Black");
        galaxy.setDescription("Newest Samsung Galaxy with Android OS.");
        galaxy.setPrice(BigDecimal.valueOf(769));
        galaxy.setQuantity(30);
        galaxy.setCategory(electronics);
        galaxy.setBrand(samsung);
        galaxy.setImageUrl("/images/Samsung-Galaxy-S24-8-GB-256-GB-black.jpg");
        productRepository.save(galaxy);

        Product pixel = new Product();
        pixel.setName("Google Pixel 9 Pro 256 GB Obsidian\n");
        pixel.setDescription("The latest flagship smartphone from Google.");
        pixel.setPrice(BigDecimal.valueOf(1220));
        pixel.setQuantity(40);
        pixel.setCategory(electronics);
        pixel.setBrand(google);
        pixel.setImageUrl("/images/Google-Pixel-9-Pro-256-GB-Obsidian.jpg");
        productRepository.save(pixel);

        Product macbook = new Product();
        macbook.setName("MacBook Pro 16\" M3 PRO 2023");
        macbook.setDescription("High-performance laptop from Apple.");
        macbook.setPrice(BigDecimal.valueOf(3179));
        macbook.setQuantity(20);
        macbook.setCategory(laptops);
        macbook.setBrand(apple);
        macbook.setImageUrl("/images/MacBook-Pro-16-M3-PRO-2023.jpg");
        productRepository.save(macbook);

        Product dellXPS = new Product();
        dellXPS.setName("Dell XPS 15 2024");
        dellXPS.setDescription("High-end laptop from Dell with a sleek design.");
        dellXPS.setPrice(BigDecimal.valueOf(2199));
        dellXPS.setQuantity(25);
        dellXPS.setCategory(laptops);
        dellXPS.setBrand(dell);
        dellXPS.setImageUrl("/images/Dell-XPS-15-2024.jpg");
        productRepository.save(dellXPS);

        Product asusRog = new Product();
        asusRog.setName("ASUS ROG Zephyrus G16");
        asusRog.setDescription("ASUS ROG Zephyrus G16 GU603VV-NEBULA078W. Gaming laptop with powerful performance.");
        asusRog.setPrice(BigDecimal.valueOf(3069));
        asusRog.setQuantity(15);
        asusRog.setCategory(laptops);
        asusRog.setBrand(asus);
        asusRog.setImageUrl("/images/ASUS-ROG-Zephyrus-G16.jpg");
        productRepository.save(asusRog);

        Product appleWatch = new Product();
        appleWatch.setName("Apple Watch Ultra 2");
        appleWatch.setDescription("Latest Apple Watch with advanced health features.");
        appleWatch.setPrice(BigDecimal.valueOf(819));
        appleWatch.setQuantity(40);
        appleWatch.setCategory(accessories);
        appleWatch.setBrand(apple);
        appleWatch.setImageUrl("/images/Apple-Watch-Ultra-2.jpg");
        productRepository.save(appleWatch);

        Product samsungBuds = new Product();
        samsungBuds.setName("Samsung Galaxy Buds3 Pro");
        samsungBuds.setDescription("Wireless earbuds with noise cancellation.");
        samsungBuds.setPrice(BigDecimal.valueOf(249));
        samsungBuds.setQuantity(60);
        samsungBuds.setCategory(accessories);
        samsungBuds.setBrand(samsung);
        samsungBuds.setImageUrl("/images/samsung-galaxy-buds3-pro.jpg");
        productRepository.save(samsungBuds);

        Product xiaomi14 = new Product();
        xiaomi14.setName("Xiaomi 14 Ultra 16 GB/512 GB Black");
        xiaomi14.setDescription("The latest flagship smartphone from Xiaomi");
        xiaomi14.setPrice(BigDecimal.valueOf(1119));
        xiaomi14.setQuantity(50);
        xiaomi14.setCategory(electronics);
        xiaomi14.setBrand(xiaomi);
        xiaomi14.setImageUrl("/images/Xiaomi-14-Ultra.jpg");
        productRepository.save(xiaomi14);

    }
}
