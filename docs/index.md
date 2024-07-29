--- 
hide: -navigation -toc 
---

# Composables Core

Unstyled, fully accessible components for Jetpack Compose & Compose Multiplatform that you can customize to your heart's content.

Available for 🖥️ Desktop, 🌐 Web (Js/WASM), 🤖 Android, 🍎 iOS, and any other platform Compose can run on.

<style>
  img {
    border-radius: 20px;
  }
  .image-grid {
    margin-top: 80px;
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 20px;
    text-align: center;
  }
  @media (max-width: 900px) {
    .image-grid {
      grid-template-columns: 1fr 1fr;
    }
  }

  @media (max-width: 600px) {
    .image-grid {
      grid-template-columns: 1fr;
    }
  }
</style>

<div class="image-grid">
    <div>
        <a href="bottom-sheet">
          <img src="preview_bottom_sheet.png" alt="Bottom Sheet Preview">
        </a>
        <div>Bottom Sheet</div>
    </div>

    <div>
        <a href="modal-bottom-sheet">
          <img src="preview_modal_bottom_sheet.png" alt="Bottom Sheet (Modal) Preview">
        </a>
        <div>Bottom Sheet (Modal)</div>
    </div>

  <div>
    <a href="dialog">
      <img src="preview_dialog.png" alt="Dialog Preview">
    </a>
    <div>Dialog</div>
  </div>

  <div>
    <a href="menu">
      <img src="preview_menu.png" alt="Menu Preview">
    </a>
    <div>Dropdown Menu</div>
  </div>

  <div>
    <a href="icon">
      <img src="preview_icon.png" alt="Icon Preview">
    </a>
    <div>Icon</div>
  </div>
</div>
