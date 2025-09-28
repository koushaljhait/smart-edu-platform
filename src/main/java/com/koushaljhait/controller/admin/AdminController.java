
package com.koushaljhait.controller.admin;

import com.koushaljhait.model.user.User;
import com.koushaljhait.repository.user.UserRepository;
import com.koushaljhait.service.common.ProductionEmailService;
import com.koushaljhait.service.common.UsernameGeneratorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsernameGeneratorService usernameGeneratorService;
    private final ProductionEmailService emailService;

    public AdminController(UserRepository userRepository, 
                         PasswordEncoder passwordEncoder,
                         UsernameGeneratorService usernameGeneratorService,
                         ProductionEmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.usernameGeneratorService = usernameGeneratorService;
        this.emailService = emailService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        Optional<User> currentUser = userRepository.findByUsername(principal.getName());
        boolean isSuperAdmin = currentUser.map(user -> user.getRole().equals("SUPER_ADMIN")).orElse(false);
        
        long totalUsers = userRepository.count();
        long teacherCount = userRepository.countByRole("TEACHER");
        long studentCount = userRepository.countByRole("STUDENT");
        long adminCount = userRepository.countByRole("ADMIN");
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("teacherCount", teacherCount);
        model.addAttribute("studentCount", studentCount);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("isSuperAdmin", isSuperAdmin);
        
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model, Principal principal) {
        try {
            Optional<User> currentUserOpt = userRepository.findByUsername(principal.getName());
            
            if (currentUserOpt.isEmpty()) {
                return "redirect:/auth/login";
            }
            
            User currentUser = currentUserOpt.get();
            boolean isSuperAdmin = currentUser.getRole().equals("SUPER_ADMIN");
            
            List<User> users;
            if (isSuperAdmin) {
                users = userRepository.findAll();
            } else {
                users = userRepository.findAll().stream()
                        .filter(user -> !user.getRole().equals("SUPER_ADMIN"))
                        .collect(Collectors.toList());
            }
            
            model.addAttribute("principal", principal);
            model.addAttribute("users", users);
            model.addAttribute("isSuperAdmin", isSuperAdmin);
            return "admin/users";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/dashboard?error=user-load-failed";
        }
    }

    @GetMapping("/create-admin")
    public String showCreateAdminForm(Model model, Principal principal) {
        Optional<User> currentUser = userRepository.findByUsername(principal.getName());
        if (currentUser.isEmpty() || !currentUser.get().getRole().equals("SUPER_ADMIN")) {
            return "redirect:/admin/dashboard?error=access-denied";
        }
        
        model.addAttribute("user", new User());
        return "admin/create-admin";
    }

    @PostMapping("/create-admin")
    public String createAdmin(@ModelAttribute User user, 
                            RedirectAttributes redirectAttributes,
                            Principal principal) {
        try {
            // Verify current user is super admin
            Optional<User> currentUser = userRepository.findByUsername(principal.getName());
            if (currentUser.isEmpty() || !currentUser.get().getRole().equals("SUPER_ADMIN")) {
                redirectAttributes.addFlashAttribute("error", "Access denied. Super admin privileges required.");
                return "redirect:/admin/dashboard";
            }

            // Validate input
            if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "First name is required.");
                return "redirect:/admin/create-admin";
            }

            if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Last name is required.");
                return "redirect:/admin/create-admin";
            }

            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Username is required.");
                return "redirect:/admin/create-admin";
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email is required.");
                return "redirect:/admin/create-admin";
            }

            // Check if email is valid format
            if (!isValidEmail(user.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Invalid email format.");
                return "redirect:/admin/create-admin";
            }

            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername().trim())) {
                redirectAttributes.addFlashAttribute("error", "Username already exists.");
                redirectAttributes.addFlashAttribute("user", user);
                return "redirect:/admin/create-admin";
            }

            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail().trim())) {
                redirectAttributes.addFlashAttribute("error", "Email already registered.");
                redirectAttributes.addFlashAttribute("user", user);
                return "redirect:/admin/create-admin";
            }

            // Generate temporary password
            String tempPassword = generateTemporaryPassword();
            String encodedPassword = passwordEncoder.encode(tempPassword);

            // Create admin user
            User newAdmin = new User();
            newAdmin.setUsername(user.getUsername().trim());
            newAdmin.setEmail(user.getEmail().trim());
            newAdmin.setFirstName(user.getFirstName().trim());
            newAdmin.setLastName(user.getLastName().trim());
            newAdmin.setPasswordHash(encodedPassword);
            newAdmin.setRole("ADMIN");
            newAdmin.setEnabled(true);
            newAdmin.setCreatedAt(LocalDateTime.now());
            newAdmin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(newAdmin);

            // Send credentials email to the new admin
            try {
                emailService.sendAdminWelcomeEmail(
                    user.getEmail().trim(), 
                    user.getUsername().trim(), 
                    user.getFirstName().trim(), 
                    tempPassword
                );
                
                redirectAttributes.addFlashAttribute("success", 
                    "✅ Administrator created successfully! Login credentials sent to " + user.getEmail());
                    
            } catch (Exception emailException) {
                // If email fails, show temporary password as fallback
                redirectAttributes.addFlashAttribute("warning", 
                    "⚠️ Administrator created, but email failed. Temporary password: " + tempPassword);
                System.err.println("Email sending failed: " + emailException.getMessage());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "❌ Error creating administrator: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, 
                           RedirectAttributes redirectAttributes,
                           Principal principal) {
        try {
            Optional<User> currentUserOpt = userRepository.findByUsername(principal.getName());
            Optional<User> targetUserOpt = userRepository.findById(id);
            
            if (currentUserOpt.isEmpty() || targetUserOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/admin/users";
            }
            
            User currentUser = currentUserOpt.get();
            User targetUser = targetUserOpt.get();
            
            // Prevent self-deletion
            if (currentUser.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete your own account.");
                return "redirect:/admin/users";
            }
            
            // Check if user is student or teacher (basic role check)
            boolean isStudentOrTeacher = targetUser.getRole().equals("STUDENT") || 
                                       targetUser.getRole().equals("TEACHER");
            
            // Check if user is admin (but not super admin)
            boolean isRegularAdmin = targetUser.getRole().equals("ADMIN");
            
            // Super admin can delete anyone except themselves
            if (currentUser.getRole().equals("SUPER_ADMIN")) {
                userRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            } 
            // Regular admin can only delete students and teachers
            else if (currentUser.getRole().equals("ADMIN") && isStudentOrTeacher) {
                userRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Insufficient permissions to delete this user.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    private String generateTemporaryPassword() {
        // Generate a random 12-character password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return password.toString();
    }

    private boolean isValidEmail(String email) {
        // Basic email validation
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
