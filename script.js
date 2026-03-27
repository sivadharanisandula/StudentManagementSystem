const BASE = "http://localhost:9090";

window.onload = () => showTab('home');

function showTab(tab) {
    document.querySelectorAll(".tab").forEach(t => t.style.display = "none");
    document.getElementById(tab).style.display = "block";

    if (tab === "view") loadStudents();
}

function addStudent() {
    let id = document.getElementById("id").value;
    let name = document.getElementById("name").value;
    let dept = document.getElementById("dept").value;

    fetch(BASE + "/add", {
        method: "POST",
        body: `${id},${name},${dept}`
    }).then(() => {
        alert("Student Added!");
        document.getElementById("id").value = "";
        document.getElementById("name").value = "";
        document.getElementById("dept").value = "";
    });
}
function loadStudents() {
    fetch(BASE + "/students")
        .then(res => res.text())
        .then(data => {
            let container = document.getElementById("list");
            container.innerHTML = "";

            let students = data ? data.split(",") : [];

            students.forEach((s, i) => {
                if (!s.trim()) return; // skip empty

                let parts = s.split("|");

                if (parts.length !== 3) return; // safety

                let id = parts[0];
                let name = parts[1];
                let dept = parts[2];

                let card = document.createElement("div");
                card.className = "student-card";

                card.innerHTML = `
                    <h3>${name}</h3>
                    <p><b>ID:</b> ${id}</p>
                    <p><b>Department:</b> ${dept}</p>
                    <button class="delete" onclick="deleteStudent(${i})">Delete</button>
                    <button class="update" onclick="updateStudent(${i})">Update</button>
                `;

                container.appendChild(card);
            });
        });
}
function deleteStudent(i) {
    fetch(BASE + "/delete?id=" + i)
        .then(() => loadStudents());
}

function updateStudent(i) {
    let id = prompt("New ID:");
    let name = prompt("New Name:");
    let dept = prompt("New Dept:");

    fetch(BASE + "/update?id=" + i, {
        method: "POST",
        body: `${id},${name},${dept}`
    }).then(() => loadStudents());
}