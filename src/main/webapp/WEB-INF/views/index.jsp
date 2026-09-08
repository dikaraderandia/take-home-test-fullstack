<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products - Product Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/" class="navbar-brand">Product Management</a>
        <ul class="navbar-nav">
            <li><a href="${pageContext.request.contextPath}/">Products</a></li>
            <li><a href="${pageContext.request.contextPath}/products/new">Add Product</a></li>
        </ul>
    </nav>

    <div class="container">
        <div class="page-header">
            <h1>Products</h1>
            <a href="${pageContext.request.contextPath}/products/new" class="btn btn-primary">+ Add Product</a>
        </div>

        <div id="errorAlert" class="alert alert-danger" style="display:none;"></div>

        <div class="card">
            <form id="searchForm" class="search-bar">
                <input type="text" id="searchName" class="form-control" placeholder="Search by name...">
                <input type="number" id="minPrice" class="form-control" min="0" step="0.01"
                       placeholder="Min price" style="flex: 0 0 140px;">
                <input type="number" id="maxPrice" class="form-control" min="0" step="0.01"
                       placeholder="Max price" style="flex: 0 0 140px;">
                <button type="submit" class="btn btn-primary">Search</button>
                <button type="button" class="btn btn-secondary" onclick="resetSearch()">Reset</button>
            </form>
        </div>

        <div class="card">
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Price</th>
                            <th>Created At</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="productTable">
                        <tr>
                            <td colspan="5" style="text-align: center; padding: 2rem; color: #888;">
                                Loading products...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="pagination" id="pagination"></div>
        </div>
    </div>

    <script>const CTX = '${pageContext.request.contextPath}'</script>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
    <script>
        var currentPage = 0;
        var limit = 10;

        window.addEventListener('DOMContentLoaded', function () {
            loadProducts(0);
        });

        document.getElementById('searchForm').addEventListener('submit', function (e) {
            e.preventDefault();
            loadProducts(0);
        });

        function resetSearch() {
            document.getElementById('searchName').value = '';
            document.getElementById('minPrice').value = '';
            document.getElementById('maxPrice').value = '';
            loadProducts(0);
        }

        async function loadProducts(page) {
            currentPage = page;

            var name = document.getElementById('searchName').value.trim();
            var minPrice = document.getElementById('minPrice').value;
            var maxPrice = document.getElementById('maxPrice').value;

            var params = new URLSearchParams({ page: page, limit: limit });
            if (name) params.set('name', name);
            if (minPrice) params.set('minPrice', minPrice);
            if (maxPrice) params.set('maxPrice', maxPrice);

            try {
                var data = await apiFetch('/products?' + params.toString());
                renderTable(data.content);
                renderPagination(data.number, data.totalPages);
            } catch (err) {
                document.getElementById('errorAlert').textContent = err.message;
                document.getElementById('errorAlert').style.display = 'block';
            }
        }

        function renderTable(rows) {
            var tbody = document.getElementById('productTable');
            tbody.innerHTML = '';

            if (!rows.length) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:2rem;color:#888;">No products found.</td></tr>';
                return;
            }

            rows.forEach(function (p, index) {
                var tr = document.createElement('tr');
                tr.innerHTML =
                    '<td>' + (currentPage * limit + index + 1) + '</td>' +
                    '<td><a href="' + CTX + '/products/' + p.id + '/edit" style="color:#2c3e87;">' + escapeHtml(p.name) + '</a></td>' +
                    '<td>' + formatPrice(p.price) + '</td>' +
                    '<td>' + new Date(p.createdAt).toLocaleString() + '</td>' +
                    '<td><div class="table-actions">' +
                    '<a class="btn btn-sm btn-primary" href="' + CTX + '/products/' + p.id + '/edit">Edit</a>' +
                    '<button type="button" class="btn btn-sm btn-danger" onclick="deleteProduct(' + p.id + ')">Delete</button>' +
                    '</div></td>';
                tbody.appendChild(tr);
            });
        }

        async function deleteProduct(id) {
            if (!confirm('Are you sure you want to delete this product?')) return;
            try {
                await apiFetch('/products/' + id, { method: 'DELETE' });
                loadProducts(currentPage);
            } catch (err) {
                document.getElementById('errorAlert').textContent = err.message;
                document.getElementById('errorAlert').style.display = 'block';
            }
        }

        function renderPagination(page, totalPages) {
            var pagination = document.getElementById('pagination');
            pagination.innerHTML = '';

            if (totalPages <= 1) return;

            var prev = document.createElement('a');
            if (page > 0) {
                prev.href = '#';
                prev.textContent = 'Prev';
                prev.onclick = function (e) { e.preventDefault(); loadProducts(page - 1); };
            } else {
                prev.textContent = 'Prev';
                prev.className = 'disabled';
            }
            pagination.appendChild(prev);

            for (var i = 0; i < totalPages; i++) {
                var a = document.createElement('a');
                a.href = '#';
                a.textContent = i + 1;
                if (i === page) a.className = 'active';
                a.onclick = (function (p) {
                    return function (e) { e.preventDefault(); loadProducts(p); };
                })(i);
                pagination.appendChild(a);
            }

            var next = document.createElement('a');
            if (page < totalPages - 1) {
                next.href = '#';
                next.textContent = 'Next';
                next.onclick = function (e) { e.preventDefault(); loadProducts(page + 1); };
            } else {
                next.textContent = 'Next';
                next.className = 'disabled';
            }
            pagination.appendChild(next);
        }
    </script>
</body>
</html>