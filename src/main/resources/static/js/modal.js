async function findUserById(id) {
    let response = await fetch("http://localhost:8080/api/admin/users/" + id);
    return await response.json();
}

async function open_fill_modal(form, modal, id) {
    modal.show();
    let user = await findUserById(id);
    form.id.value = user.id;
    form.username.value = user.username;
    form.firstname.value = user.firstname;
    form.lastname.value = user.lastname;
    form.email.value = user.email;
    form.password.value = user.password;
    form.roles.value = user.roles;
}