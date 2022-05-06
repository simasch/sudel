package app.sudel.configuration.security;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static app.sudel.db.tables.SecurityUser.SECURITY_USER;
import static app.sudel.db.tables.UserGroup.USER_GROUP;

@Component
public class AdminCreator implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminCreator.class);

    private final DSLContext ctx;
    private final PasswordEncoder passwordEncoder;

    public AdminCreator(DSLContext ctx, PasswordEncoder passwordEncoder) {
        this.ctx = ctx;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {
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
