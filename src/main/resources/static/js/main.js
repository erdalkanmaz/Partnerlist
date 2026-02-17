// Ana JavaScript dosyası

// Flash mesajlarını otomatik kapat
document.addEventListener('DOMContentLoaded', function() {
    const flashMessages = document.querySelectorAll('.flash-message');
    flashMessages.forEach(function(message) {
        setTimeout(function() {
            message.style.opacity = '0';
            setTimeout(function() {
                message.remove();
            }, 300);
        }, 5000);
    });
});

// Form validasyonu
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    
    requiredFields.forEach(function(field) {
        if (!field.value.trim()) {
            isValid = false;
            field.classList.add('error');
        } else {
            field.classList.remove('error');
        }
    });
    
    return isValid;
}

// Checkbox seçim sayacı
function updateCheckboxCount(checkboxClass, countElementId) {
    const checkboxes = document.querySelectorAll('.' + checkboxClass + ':checked');
    const count = checkboxes.length;
    const countElement = document.getElementById(countElementId);
    if (countElement) {
        countElement.textContent = count + ' seçildi';
    }
    return count;
}

// Tüm checkbox'ları seç/kaldır
function toggleAllCheckboxes(checkboxClass, toggleButton) {
    const checkboxes = document.querySelectorAll('.' + checkboxClass);
    const isChecked = toggleButton.checked;
    checkboxes.forEach(function(checkbox) {
        checkbox.checked = isChecked;
    });
}

// Arama formu submit
function handleSearch(event) {
    const searchTerm = event.target.querySelector('input[name="search"]').value.trim();
    if (!searchTerm) {
        event.preventDefault();
        alert('Lütfen arama terimi girin.');
        return false;
    }
    return true;
}

// Silme onayı
function confirmDelete(message) {
    return confirm(message || 'Bu kaydı silmek istediğinizden emin misiniz?');
}

// Yazdırma
function printPage() {
    window.print();
}

// Sayfa yüklendiğinde
document.addEventListener('DOMContentLoaded', function() {
    console.log('Partner List Management System yüklendi.');
});
