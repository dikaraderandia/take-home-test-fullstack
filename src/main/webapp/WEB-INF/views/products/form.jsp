<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Form - Product Management</title>
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
            <h1 id="pageTitle">Create New Product</h1>
        </div>

        <div id="errorAlert" class="alert alert-danger" style="display:none;"></div>

        <div class="card" style="max-width: 700px;">            <form id="productForm">
                <div class="form-group">
                    <label for="name">Name *</label>
                    <input type="text" id="name" class="form-control"
                           placeholder="Enter product name" required>
                </div>

                <div class="form-group">
                    <label for="price">Price *</label>
                    <input type="number" id="price" class="form-control"
                           step="0.01" min="0.01" placeholder="0.00" required>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" class="form-control" rows="4"
                              placeholder="Enter product description"></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary" id="saveButton">Create Product</button>
                    <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Back to List</a>
                </div>
            </form>
        </div>
    </div>

    <script>const CTX = '${pageContext.request.contextPath}'</script>
    <script>
        const EDIT_MODE = ${isEdit};
        const PRODUCT_ID = ${productId != null ? productId : 'null'};
    </script>
    <script src="${pageContext.request.contextPath}/js/app.js"></script>
    <script>
        window.addEventListener('DOMContentLoaded', function () {
            if (EDIT_MODE) {
                document.getElementById('pageTitle').textContent = 'Edit Product';
                document.getElementById('saveButton').textContent = 'Update Product';

                apiFetch('/products/' + PRODUCT_ID).then(function (p) {
                    document.getElementById('name').value = p.name || '';
                    document.getElementById('price').value = p.price || '';
                    document.getElementById('description').value = p.description || '';
                }).catch(function (err) {
                    document.getElementById('errorAlert').textContent = err.message;
                    document.getElementById('errorAlert').style.display = 'block';
                });
            }
        });

        document.getElementById('productForm').addEventListener('submit', async function (e) {
            e.preventDefault();

            var body = JSON.stringify({
                name: document.getElementById('name').value.trim(),
                price: Number(document.getElementById('price').value),
                description: document.getElementById('description').value.trim()
            });

            try {
                if (EDIT_MODE) {
                    await apiFetch('/products/' + PRODUCT_ID, { method: 'PUT', body: body });
                } else {
                    await apiFetch('/products', { method: 'POST', body: body });
                }
                window.location.href = CTX + '/';
            } catch (err) {
                document.getElementById('errorAlert').textContent = err.message;
                document.getElementById('errorAlert').style.display = 'block';
            }
        });
    </script>
</body>
</html>