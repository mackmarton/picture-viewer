let currentUser = null;
let currentSort = 'name';
let currentPictureId = null;

(async () => {
  await checkAuth();
  await loadPictures();
})();

function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.getElementById('page-' + name).classList.add('active');
  clearFlash();
}

function flash(msg, type = 'success') {
  const el = document.getElementById('flash');
  el.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${msg}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
}

function clearFlash() {
  document.getElementById('flash').innerHTML = '';
}

async function checkAuth() {
  try {
    const res = await fetch('/api/auth/me', {credentials: 'include'});
    if (res.ok) {
      const data = await res.json();
      setUser(data.username);
    } else {
      setUser(null);
    }
  } catch {
    setUser(null);
  }
}

function setUser(username) {
  currentUser = username;
  const guest = document.getElementById('nav-guest');
  const user = document.getElementById('nav-user');
  const uploadCard = document.getElementById('upload-card');

  if (username) {
    guest.classList.add('d-none');
    user.classList.remove('d-none');
    document.getElementById('nav-username').textContent = username;
    uploadCard.classList.remove('d-none');
  } else {
    guest.classList.remove('d-none');
    user.classList.add('d-none');
    uploadCard.classList.add('d-none');
  }
}

async function login(event) {
  event.preventDefault();
  const username = document.getElementById('login-username').value;
  const password = document.getElementById('login-password').value;

  const res = await fetch('/api/auth/login', {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
      'Accept': 'application/json;charset=UTF-8'
    },
    body: new URLSearchParams({username, password})
  });

  const data = await res.json();
  if (res.ok) {
    setUser(data.username);
    showPage('list');
    flash(`Üdv, ${data.username}!`);
    await loadPictures();
  } else {
    flash(data.error || 'Hiba a bejelentkezés során.', 'danger');
  }
}

async function logout() {
  await fetch('/api/auth/logout', {method: 'POST', credentials: 'include'});
  setUser(null);
  showPage('list');
  flash('Sikeresen kijelentkeztél.');
  await loadPictures();
}

async function register(event) {
  event.preventDefault();
  const password = document.getElementById('reg-password').value;
  const confirm = document.getElementById('reg-confirm').value;

  if (password !== confirm) {
    flash('A két jelszó nem egyezik!', 'danger');
    return;
  }

  const res = await fetch('/api/auth/register', {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8',
      'Accept': 'application/json;charset=UTF-8'
    },
    body: JSON.stringify({
      username: document.getElementById('reg-username').value,
      email: document.getElementById('reg-email').value,
      password,
      confirmPassword: confirm
    })
  });

  const data = await res.json();
  if (res.ok) {
    showPage('login');
    flash(data.message || 'Sikeres regisztráció! Jelentkezz be.');
  } else {
    flash(data.error || JSON.stringify(data), 'danger');
  }
}

async function loadPictures() {
  const res = await fetch(`/api/pictures?sort=${currentSort}`, {credentials: 'include'});
  const pictures = await res.json();
  renderPictures(pictures);
}

function sortBy(sort) {
  currentSort = sort;
  loadPictures();
}

function renderPictures(pictures) {
  const grid = document.getElementById('picture-grid');
  const empty = document.getElementById('no-pictures');

  if (!pictures.length) {
    grid.innerHTML = '';
    empty.classList.remove('d-none');
    return;
  }
  empty.classList.add('d-none');

  grid.innerHTML = pictures.map(p => `
        <div class="col">
            <div class="card h-100 shadow-sm">
                <img src="${p.imageUrl}" alt="${p.name}" class="card-img-top"
                     onclick="openDetail(${p.id})"/>
                <div class="card-body">
                    <h6 class="card-title text-truncate">${p.name}</h6>
                    <p class="card-text text-muted small">${formatDate(p.uploadedAt)}</p>
                    <p class="card-text text-muted small">👤 ${p.ownerUsername}</p>
                </div>
                ${currentUser ? `
                <div class="card-footer">
                    <button class="btn btn-sm btn-outline-danger w-100"
                            onclick="confirmDelete(${p.id})">Törlés</button>
                </div>` : ''}
            </div>
        </div>`).join('');
}

async function openDetail(id) {
  const res = await fetch(`/api/pictures/${id}`, {credentials: 'include'});
  const p = await res.json();
  currentPictureId = id;

  document.getElementById('detail-name').textContent = p.name;
  document.getElementById('detail-meta').textContent =
    `Feltöltve: ${formatDate(p.uploadedAt)} · Feltöltő: ${p.ownerUsername}`;
  document.getElementById('detail-img').src = p.imageUrl;
  document.getElementById('detail-img').alt = p.name;

  const deleteBtn = document.getElementById('detail-delete-btn');
  deleteBtn.classList.toggle('d-none', !currentUser);

  showPage('detail');
}

async function confirmDelete(id) {
  if (!confirm('Biztosan törlöd ezt a képet?')) return;
  currentPictureId = id;
  await deletePicture();
}

async function deletePicture() {
  if (!confirm('Biztosan törlöd ezt a képet?')) return;
  const res = await fetch(`/api/pictures/${currentPictureId}`, {
    method: 'DELETE',
    credentials: 'include'
  });

  if (res.status === 204) {
    showPage('list');
    flash('Kép sikeresen törölve.');
    await loadPictures();
  } else {
    const data = await res.json().catch(() => ({}));
    flash(data.error || 'Hiba a törlés során.', 'danger');
  }
}

async function uploadPicture(event) {
  event.preventDefault();
  const name = document.getElementById('upload-name').value.trim();
  const file = document.getElementById('upload-file').files[0];

  if (!file) {
    flash('Válassz ki egy képfájlt!', 'danger');
    return;
  }

  const spinner = document.getElementById('upload-spinner');
  spinner.classList.remove('d-none');

  const formData = new FormData();
  formData.append('name', name);
  formData.append('file', file);

  const res = await fetch('/api/pictures', {
    method: 'POST',
    credentials: 'include',
    body: formData
  });

  spinner.classList.add('d-none');

  if (res.status === 201) {
    document.getElementById('upload-form').reset();
    flash('Kép sikeresen feltöltve!');
    await loadPictures();
  } else {
    const data = await res.json().catch(() => ({}));
    flash(data.error || 'Hiba a feltöltés során.', 'danger');
  }
}

function formatDate(isoString) {
  if (!isoString) return '';
  const d = new Date(isoString);
  return d.getFullYear() + '-' +
    String(d.getMonth() + 1).padStart(2, '0') + '-' +
    String(d.getDate()).padStart(2, '0') + ' ' +
    String(d.getHours()).padStart(2, '0') + ':' +
    String(d.getMinutes()).padStart(2, '0');
}

