(() => {
  "use strict";

  const API = "/api";

  // Every browser gets its own anonymous "cart owner id" so the cart persists
  // across page reloads without needing a login system.
  function getCartOwnerId() {
    let id = localStorage.getItem("furrow_cart_owner_id");
    if (!id) {
      id = "cart-" + Math.random().toString(36).slice(2) + Date.now().toString(36);
      localStorage.setItem("furrow_cart_owner_id", id);
    }
    return id;
  }
  const cartOwnerId = getCartOwnerId();

  const rupee = (n) => "\u20B9" + Number(n).toFixed(0);

  // ---- DOM references ----
  const productGrid = document.getElementById("productGrid");
  const categoryList = document.getElementById("categoryList");
  const sectionTitle = document.getElementById("sectionTitle");
  const resultCount = document.getElementById("resultCount");
  const searchInput = document.getElementById("searchInput");

  const cartToggle = document.getElementById("cartToggle");
  const cartDrawer = document.getElementById("cartDrawer");
  const cartOverlay = document.getElementById("cartOverlay");
  const cartClose = document.getElementById("cartClose");
  const cartItemsEl = document.getElementById("cartItems");
  const cartSubtotalEl = document.getElementById("cartSubtotal");
  const cartCountEl = document.getElementById("cartCount");
  const checkoutBtn = document.getElementById("checkoutBtn");

  const checkoutPanel = document.getElementById("checkoutPanel");
  const checkoutOverlay = document.getElementById("checkoutOverlay");
  const checkoutClose = document.getElementById("checkoutClose");
  const checkoutForm = document.getElementById("checkoutForm");
  const checkoutTotalEl = document.getElementById("checkoutTotal");
  const orderConfirmation = document.getElementById("orderConfirmation");
  const confirmationDetail = document.getElementById("confirmationDetail");
  const continueShoppingBtn = document.getElementById("continueShoppingBtn");

  let allProducts = [];
  let activeCategory = "All";
  let cartState = { items: [], itemCount: 0, total: 0 };

  // ---- Data loading ----

  async function loadCategories() {
    const res = await fetch(`${API}/products/categories`);
    const categories = await res.json();
    categoryList.innerHTML = "";

    const makePill = (name) => {
      const li = document.createElement("li");
      const btn = document.createElement("button");
      btn.className = "category-pill" + (name === activeCategory ? " active" : "");
      btn.textContent = name;
      btn.addEventListener("click", () => {
        activeCategory = name;
        searchInput.value = "";
        loadProducts();
        document.querySelectorAll(".category-pill").forEach((el) => el.classList.remove("active"));
        btn.classList.add("active");
      });
      li.appendChild(btn);
      categoryList.appendChild(li);
    };

    makePill("All");
    categories.forEach(makePill);
  }

  async function loadProducts() {
    productGrid.innerHTML = `<p class="loading">Loading the shelves…</p>`;
    const search = searchInput.value.trim();
    const params = new URLSearchParams();
    if (search) params.set("search", search);
    else if (activeCategory !== "All") params.set("category", activeCategory);

    const res = await fetch(`${API}/products?${params.toString()}`);
    allProducts = await res.json();
    sectionTitle.textContent = search ? `Results for "${search}"` : activeCategory === "All" ? "All produce" : activeCategory;
    resultCount.textContent = `${allProducts.length} item${allProducts.length === 1 ? "" : "s"}`;
    renderProducts();
  }

  function quantityInCart(productId) {
    const line = cartState.items.find((i) => i.product.id === productId);
    return line ? line.quantity : 0;
  }

  function renderProducts() {
    if (allProducts.length === 0) {
      productGrid.innerHTML = `<p class="loading">No products match that search.</p>`;
      return;
    }
    productGrid.innerHTML = "";
    allProducts.forEach((product) => {
      const card = document.createElement("div");
      card.className = "product-card";

      const qty = quantityInCart(product.id);
      const outOfStock = product.stock <= 0;

      card.innerHTML = `
        <div class="product-emoji">${product.imageEmoji || "\uD83D\uDED2"}</div>
        <div class="product-name">${escapeHtml(product.name)}</div>
        <div class="product-unit">per ${escapeHtml(product.unit || "unit")} &middot; ${product.stock} in stock</div>
        <div class="product-price">${rupee(product.price)}</div>
        <div class="product-footer"></div>
      `;

      const footer = card.querySelector(".product-footer");
      if (outOfStock) {
        footer.innerHTML = `<span class="out-of-stock">Out of stock</span>`;
      } else if (qty > 0) {
        footer.appendChild(buildStepper(product, qty));
      } else {
        const btn = document.createElement("button");
        btn.className = "add-btn";
        btn.textContent = "Add";
        btn.addEventListener("click", () => addToCart(product.id, 1));
        footer.appendChild(btn);
      }

      productGrid.appendChild(card);
    });
  }

  function buildStepper(product, qty) {
    const wrap = document.createElement("div");
    wrap.className = "stepper";

    const minus = document.createElement("button");
    minus.textContent = "\u2212";
    minus.addEventListener("click", () => changeCartLineByProduct(product.id, qty - 1));

    const count = document.createElement("span");
    count.textContent = qty;

    const plus = document.createElement("button");
    plus.textContent = "+";
    plus.addEventListener("click", () => addToCart(product.id, 1));

    wrap.append(minus, count, plus);
    return wrap;
  }

  function escapeHtml(str) {
    const div = document.createElement("div");
    div.textContent = str;
    return div.innerHTML;
  }

  // ---- Cart operations ----

  async function refreshCart() {
    const res = await fetch(`${API}/cart/${cartOwnerId}`);
    cartState = await res.json();
    renderCart();
    renderProducts();
  }

  async function addToCart(productId, quantity) {
    await fetch(`${API}/cart/${cartOwnerId}/items`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ productId, quantity }),
    });
    await refreshCart();
  }

  async function changeCartLineByProduct(productId, newQuantity) {
    const line = cartState.items.find((i) => i.product.id === productId);
    if (!line) return;
    await updateCartLine(line.id, newQuantity);
  }

  async function updateCartLine(itemId, newQuantity) {
    await fetch(`${API}/cart/${cartOwnerId}/items/${itemId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ quantity: newQuantity }),
    });
    await refreshCart();
  }

  async function removeCartLine(itemId) {
    await fetch(`${API}/cart/${cartOwnerId}/items/${itemId}`, { method: "DELETE" });
    await refreshCart();
  }

  function renderCart() {
    cartCountEl.textContent = cartState.itemCount || 0;
    cartSubtotalEl.textContent = rupee(cartState.total || 0);
    checkoutTotalEl.textContent = rupee(cartState.total || 0);
    checkoutBtn.disabled = !cartState.items || cartState.items.length === 0;

    if (!cartState.items || cartState.items.length === 0) {
      cartItemsEl.innerHTML = `<p class="cart-empty">Your basket is empty. Add something fresh.</p>`;
      return;
    }

    cartItemsEl.innerHTML = "";
    cartState.items.forEach((line) => {
      const row = document.createElement("div");
      row.className = "cart-line";
      row.innerHTML = `
        <div class="cart-line-emoji">${line.product.imageEmoji || "\uD83D\uDED2"}</div>
        <div class="cart-line-info">
          <div class="cart-line-name">${escapeHtml(line.product.name)}</div>
          <div class="cart-line-price">${line.quantity} &times; ${rupee(line.product.price)}</div>
        </div>
      `;

      const stepper = buildStepperForLine(line);
      row.appendChild(stepper);

      const removeBtn = document.createElement("button");
      removeBtn.className = "cart-line-remove";
      removeBtn.textContent = "Remove";
      removeBtn.addEventListener("click", () => removeCartLine(line.id));

      const controls = document.createElement("div");
      controls.style.display = "flex";
      controls.style.flexDirection = "column";
      controls.style.alignItems = "flex-end";
      controls.style.gap = "6px";
      controls.appendChild(stepper);
      controls.appendChild(removeBtn);
      row.appendChild(controls);

      cartItemsEl.appendChild(row);
    });
  }

  function buildStepperForLine(line) {
    const wrap = document.createElement("div");
    wrap.className = "stepper";

    const minus = document.createElement("button");
    minus.textContent = "\u2212";
    minus.addEventListener("click", () => updateCartLine(line.id, line.quantity - 1));

    const count = document.createElement("span");
    count.textContent = line.quantity;

    const plus = document.createElement("button");
    plus.textContent = "+";
    plus.addEventListener("click", () => updateCartLine(line.id, line.quantity + 1));

    wrap.append(minus, count, plus);
    return wrap;
  }

  // ---- Drawer / panel visibility ----

  function openCart() {
    cartDrawer.classList.add("open");
    cartOverlay.classList.add("visible");
    cartDrawer.setAttribute("aria-hidden", "false");
  }
  function closeCart() {
    cartDrawer.classList.remove("open");
    cartOverlay.classList.remove("visible");
    cartDrawer.setAttribute("aria-hidden", "true");
  }

  function openCheckout() {
    orderConfirmation.hidden = true;
    checkoutForm.hidden = false;
    checkoutPanel.classList.add("open");
    checkoutOverlay.classList.add("visible");
    checkoutPanel.setAttribute("aria-hidden", "false");
    closeCart();
  }
  function closeCheckout() {
    checkoutPanel.classList.remove("open");
    checkoutOverlay.classList.remove("visible");
    checkoutPanel.setAttribute("aria-hidden", "true");
  }

  cartToggle.addEventListener("click", openCart);
  cartClose.addEventListener("click", closeCart);
  cartOverlay.addEventListener("click", closeCart);

  checkoutBtn.addEventListener("click", openCheckout);
  checkoutClose.addEventListener("click", closeCheckout);
  checkoutOverlay.addEventListener("click", closeCheckout);

  continueShoppingBtn.addEventListener("click", () => {
    closeCheckout();
  });

  // ---- Checkout submission ----

  checkoutForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const customerName = document.getElementById("customerName").value.trim();
    const deliveryAddress = document.getElementById("deliveryAddress").value.trim();

    const res = await fetch(`${API}/orders/checkout`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ cartOwnerId, customerName, deliveryAddress }),
    });

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      alert(err.error || "Something went wrong placing your order.");
      return;
    }

    const order = await res.json();
    confirmationDetail.textContent =
      `Thanks, ${customerName}. Order #${order.id} totalling ${rupee(order.totalAmount)} is on its way to ${deliveryAddress}.`;
    checkoutForm.reset();
    checkoutForm.hidden = true;
    orderConfirmation.hidden = false;

    await refreshCart();
  });

  // ---- Search ----

  let searchDebounce;
  searchInput.addEventListener("input", () => {
    clearTimeout(searchDebounce);
    searchDebounce = setTimeout(loadProducts, 250);
  });

  // ---- Init ----

  (async function init() {
    await loadCategories();
    await loadProducts();
    await refreshCart();
  })();
})();
