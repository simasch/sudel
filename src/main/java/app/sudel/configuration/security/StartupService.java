package app.sudel.configuration.security;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static app.sudel.db.tables.SecurityUser.SECURITY_USER;
import static app.sudel.db.tables.UserGroup.USER_GROUP;

@Service
public class StartupService implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupService.class);

    private final DSLContext ctx;
    private final PasswordEncoder passwordEncoder;

    public StartupService(DSLContext ctx, PasswordEncoder passwordEncoder) {
        this.ctx = ctx;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        // Check if Admin user exists. If not create it.
        // Hint: This is just for local test purposes!
        var admin = ctx.selectFrom(SECURITY_USER).where(SECURITY_USER.EMAIL.eq("admin@sudel.app")).fetchOne();
        if (admin == null) {
            admin = ctx.newRecord(SECURITY_USER);
            admin.setEmail("admin@sudel.app");
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setConfirmed(true);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            LOGGER.info("admin@sudel.app password: " + uuid);
            admin.setSecret(passwordEncoder.encode(uuid));
            admin.store();

            var userGroup = ctx.newRecord(USER_GROUP);
            userGroup.setGroupId(1L);
            userGroup.setUserId(admin.getId());
            userGroup.store();
        }
    }
}
