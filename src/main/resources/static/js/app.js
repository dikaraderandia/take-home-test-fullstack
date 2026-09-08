const API_BASE = window.location.origin + '/api';

function getToken() {
    return localStorage.getItem('token');
}

function escapeHtml(value) {
    if (value === null || value === undefined) return '';
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function escapeAttr(value) {
    return escapeHtml(value).replace(/"/g, '&quot;');
}

function formatPrice(value) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(value);
}

async function apiFetch(path, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    const token = getToken();
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    const res = await fetch(API_BASE + path, {
        ...options,
        headers
    });

    if (res.status === 401) {
        localStorage.removeItem('token');
        throw new Error('Unauthorized - please provide a valid token');
    }

    if (res.status === 429) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.message || 'Too many requests - please wait 5 seconds');
    }

    if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        if (body.data && body.data.validation) {
            throw new Error(body.data.validation);
        }
        throw new Error(body.message || 'Request failed');
    }

    if (res.status === 204) {
        return null;
    }

    return res.json();
}